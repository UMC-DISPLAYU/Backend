package com.example.demo.global.event;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.events.EventPublication.Status;
import org.springframework.stereotype.Component;

@Component
public class EventPublicationMetrics {

  private final JdbcTemplate jdbcTemplate;
  private final Clock clock;
  private final Map<Status, AtomicLong> publicationCounts = new EnumMap<>(Status.class);
  private final AtomicLong oldestIncompleteAgeSeconds = new AtomicLong();
  private final AtomicLong maxAttemptsReached = new AtomicLong();
  private final Counter recoveryExecutions;
  private final Counter recoveryErrors;
  private final Counter completedDeletions;

  public EventPublicationMetrics(
      JdbcTemplate jdbcTemplate, MeterRegistry meterRegistry, Clock clock) {
    this.jdbcTemplate = jdbcTemplate;
    this.clock = clock;

    for (Status status : Status.values()) {
      AtomicLong value = new AtomicLong();
      publicationCounts.put(status, value);
      Gauge.builder("domain.event.publication.count", value, AtomicLong::get)
          .tag("status", status.name())
          .register(meterRegistry);
    }

    Gauge.builder(
            "domain.event.publication.oldest.incomplete.age.seconds",
            oldestIncompleteAgeSeconds,
            AtomicLong::get)
        .register(meterRegistry);
    Gauge.builder(
            "domain.event.publication.max.attempts.count", maxAttemptsReached, AtomicLong::get)
        .description(
            "Publications excluded from automatic recovery after reaching the attempt limit")
        .register(meterRegistry);

    recoveryExecutions =
        Counter.builder("domain.event.publication.recovery")
            .tag("result", "executed")
            .register(meterRegistry);
    recoveryErrors =
        Counter.builder("domain.event.publication.recovery")
            .tag("result", "error")
            .register(meterRegistry);
    completedDeletions =
        Counter.builder("domain.event.publication.completed.cleanup")
            .description("Completed publication rows deleted by retention cleanup")
            .register(meterRegistry);
  }

  public void recordRecoveryExecution() {
    recoveryExecutions.increment();
  }

  public void recordRecoveryError() {
    recoveryErrors.increment();
  }

  public void recordCompletedDeletions(long count) {
    completedDeletions.increment(count);
  }

  public void refresh(int maximumAttempts) {
    publicationCounts.values().forEach(value -> value.set(0));
    jdbcTemplate.query(
        "SELECT STATUS, COUNT(*) FROM EVENT_PUBLICATION GROUP BY STATUS",
        resultSet -> {
          while (resultSet.next()) {
            String statusName = resultSet.getString(1);
            if (statusName != null) {
              publicationCounts.get(Status.valueOf(statusName)).set(resultSet.getLong(2));
            }
          }
          return null;
        });

    Timestamp oldest =
        jdbcTemplate.queryForObject(
            "SELECT MIN(PUBLICATION_DATE) FROM EVENT_PUBLICATION WHERE COMPLETION_DATE IS NULL",
            Timestamp.class);
    long age =
        oldest == null
            ? 0
            : Math.max(0, Duration.between(oldest.toInstant(), Instant.now(clock)).toSeconds());
    oldestIncompleteAgeSeconds.set(age);

    Long exhausted =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM EVENT_PUBLICATION WHERE COMPLETION_DATE IS NULL AND COALESCE(COMPLETION_ATTEMPTS, 0) >= ?",
            Long.class,
            maximumAttempts);
    maxAttemptsReached.set(exhausted == null ? 0 : exhausted);
  }
}
