package com.example.demo.global.event;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.events.CompletedEventPublications;
import org.springframework.modulith.events.FailedEventPublications;
import org.springframework.modulith.events.ResubmissionOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventPublicationMaintenanceScheduler {

  private static final Logger log =
      LoggerFactory.getLogger(EventPublicationMaintenanceScheduler.class);

  private final FailedEventPublications failedPublications;
  private final CompletedEventPublications completedPublications;
  private final DomainEventPublicationProperties properties;
  private final EventPublicationMetrics metrics;
  private final JdbcTemplate jdbcTemplate;
  private final Clock clock;

  public EventPublicationMaintenanceScheduler(
      FailedEventPublications failedPublications,
      CompletedEventPublications completedPublications,
      DomainEventPublicationProperties properties,
      EventPublicationMetrics metrics,
      JdbcTemplate jdbcTemplate,
      Clock clock) {
    this.failedPublications = failedPublications;
    this.completedPublications = completedPublications;
    this.properties = properties;
    this.metrics = metrics;
    this.jdbcTemplate = jdbcTemplate;
    this.clock = clock;
  }

  @Scheduled(fixedDelayString = "${app.domain-events.publication.recovery-interval:1m}")
  public void recoverFailedPublications() {
    try {
      logRetryCandidates();
      ResubmissionOptions options =
          ResubmissionOptions.defaults()
              .withMinAge(properties.getMinimumRetryAge())
              .withBatchSize(properties.getBatchSize())
              .withMaxInFlight(properties.getMaxInFlight())
              .withFilter(
                  publication ->
                      publication.getCompletionAttempts() < properties.getMaxCompletionAttempts());
      failedPublications.resubmit(options);
      metrics.recordRecoveryExecution();
      metrics.refresh(properties.getMaxCompletionAttempts());
    } catch (RuntimeException exception) {
      metrics.recordRecoveryError();
      log.error("Failed to recover event publications", exception);
    }
  }

  @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
  public void deleteExpiredCompletedPublications() {
    try {
      Instant cutoff = Instant.now(clock).minus(properties.getCompletedRetention());
      Long deletionCount =
          jdbcTemplate.queryForObject(
              "SELECT COUNT(*) FROM EVENT_PUBLICATION WHERE COMPLETION_DATE < ?",
              Long.class,
              Timestamp.from(cutoff));
      completedPublications.deletePublicationsOlderThan(properties.getCompletedRetention());
      metrics.recordCompletedDeletions(deletionCount == null ? 0 : deletionCount);
      metrics.refresh(properties.getMaxCompletionAttempts());
    } catch (RuntimeException exception) {
      log.error("Failed to delete completed event publications", exception);
    }
  }

  private void logRetryCandidates() {
    List<RetryCandidate> candidates =
        jdbcTemplate.query(
            """
            SELECT EVENT_TYPE, LISTENER_ID, COALESCE(COMPLETION_ATTEMPTS, 0), PUBLICATION_DATE
            FROM EVENT_PUBLICATION
            WHERE STATUS = 'FAILED'
              AND COALESCE(COMPLETION_ATTEMPTS, 0) < ?
            ORDER BY PUBLICATION_DATE
            LIMIT ?
            """,
            (resultSet, rowNumber) ->
                new RetryCandidate(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getInt(3),
                    resultSet.getTimestamp(4).toInstant()),
            properties.getMaxCompletionAttempts(),
            properties.getBatchSize());

    candidates.forEach(
        candidate ->
            log.info(
                "Resubmitting event publication. eventType={} listenerId={} attempt={} publishedAt={}",
                candidate.eventType(),
                candidate.listenerId(),
                candidate.attempt(),
                candidate.publishedAt()));

    Long exhausted =
        jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM EVENT_PUBLICATION
            WHERE COMPLETION_DATE IS NULL
              AND COALESCE(COMPLETION_ATTEMPTS, 0) >= ?
            """,
            Long.class,
            properties.getMaxCompletionAttempts());
    if (exhausted != null && exhausted > 0) {
      log.warn(
          "Event publications reached the automatic recovery limit. count={} maxAttempts={}",
          exhausted,
          properties.getMaxCompletionAttempts());
    }
  }

  private record RetryCandidate(
      String eventType, String listenerId, int attempt, Instant publishedAt) {}
}
