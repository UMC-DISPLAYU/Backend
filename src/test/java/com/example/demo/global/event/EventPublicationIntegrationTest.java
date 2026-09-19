package com.example.demo.global.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;

import com.example.demo.domain.display.application.command.CleanupDeletedDisplayService;
import com.example.demo.domain.display.application.port.DisplayDeletionCleanupPort;
import com.example.demo.domain.display.contract.event.v1.DisplayDeletedEvent;
import com.example.demo.domain.display.domain.entity.DisplayLike;
import com.example.demo.domain.display.domain.repository.DisplayLikeRepository;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.event.LegacyDisplayDeletionCleanupRecoveryProcessor;
import com.example.demo.domain.display.infrastructure.persistence.DisplayDeletionCleanupFailure;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayDeletionCleanupFailureJpaRepository;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(
    properties = {
      "app.domain-events.publication.minimum-retry-age=0s",
      "app.domain-events.publication.completed-retention=30d"
    })
class EventPublicationIntegrationTest {

  @Autowired private ApplicationEventPublisher eventPublisher;
  @Autowired private PlatformTransactionManager transactionManager;
  @Autowired private JdbcTemplate jdbcTemplate;
  @Autowired private EventPublicationMaintenanceScheduler maintenanceScheduler;
  @Autowired private SpringDataDisplayDeletionCleanupFailureJpaRepository failureRepository;
  @Autowired private LegacyDisplayDeletionCleanupRecoveryProcessor legacyRecoveryProcessor;
  @Autowired private DisplayLikeRepository displayLikeRepository;
  @Autowired private CleanupDeletedDisplayService cleanupDeletedDisplayService;
  @Autowired private Clock clock;

  @MockitoBean private DisplayDeletionCleanupPort cleanupPort;

  @BeforeEach
  void setUp() {
    jdbcTemplate.update("DELETE FROM EVENT_PUBLICATION");
    failureRepository.deleteAll();
    reset(cleanupPort);
  }

  @Test
  void rollsBackPublicationWithProducerTransaction() {
    TransactionTemplate transaction = new TransactionTemplate(transactionManager);
    transaction.executeWithoutResult(
        status -> {
          eventPublisher.publishEvent(newEvent());
          status.setRollbackOnly();
        });

    assertEquals(0L, publicationCount());
  }

  @Test
  void completesPublicationAfterSubscriberTransactionSucceeds() throws Exception {
    CountDownLatch handled = new CountDownLatch(1);
    doAnswer(
            invocation -> {
              handled.countDown();
              return null;
            })
        .when(cleanupPort)
        .cleanupDisplayChildren(
            org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any());

    publishInTransaction(newEvent());

    handled.await(5, TimeUnit.SECONDS);
    awaitStatus("COMPLETED", 2L);
  }

  @Test
  void recoversFailedPublicationAndCompletesIt() throws Exception {
    CountDownLatch failed = new CountDownLatch(1);
    doAnswer(
            invocation -> {
              failed.countDown();
              throw new IllegalStateException("cleanup failed");
            })
        .when(cleanupPort)
        .cleanupDisplayChildren(
            org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any());

    publishInTransaction(newEvent());
    failed.await(5, TimeUnit.SECONDS);
    awaitStatus("FAILED", 1L);
    awaitStatus("COMPLETED", 1L);

    reset(cleanupPort);
    doNothing()
        .when(cleanupPort)
        .cleanupDisplayChildren(
            org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any());
    maintenanceScheduler.recoverFailedPublications();

    awaitStatus("COMPLETED", 2L);
  }

  @Test
  void migratesLegacyFailureByPublishingAndMarkingItRecovered() throws Exception {
    DisplayDeletionCleanupFailure failure =
        failureRepository.saveAndFlush(
            DisplayDeletionCleanupFailure.from(
                91L, LocalDateTime.of(2026, 8, 13, 12, 0), 3, new RuntimeException("legacy")));

    legacyRecoveryProcessor.recover(failure.getId());

    DisplayDeletionCleanupFailure recovered =
        failureRepository.findById(failure.getId()).orElseThrow();
    assertNotNull(recovered.getRecoveredAt());
    awaitStatus("COMPLETED", 2L);
  }

  @Test
  void deletesOnlyCompletedPublicationsOlderThanRetention() {
    insertCompletedPublication(Instant.now(clock).minusSeconds(31L * 24 * 60 * 60));
    insertCompletedPublication(Instant.now(clock).minusSeconds(29L * 24 * 60 * 60));

    maintenanceScheduler.deleteExpiredCompletedPublications();

    assertEquals(1L, publicationCount());
  }

  @Test
  void displaySubscriberUseCaseIsIdempotentWhenCleanupIsRepeated() {
    displayLikeRepository.save(DisplayLike.create(10L, new UserId(20L)));

    cleanupDeletedDisplayService.cleanup(10L);
    cleanupDeletedDisplayService.cleanup(10L);

    assertEquals(0L, displayLikeRepository.countByDisplayId(10L));
  }

  private void publishInTransaction(DisplayDeletedEvent event) {
    new TransactionTemplate(transactionManager)
        .executeWithoutResult(status -> eventPublisher.publishEvent(event));
  }

  private DisplayDeletedEvent newEvent() {
    return new DisplayDeletedEvent(UUID.randomUUID(), 10L, LocalDateTime.now(clock));
  }

  private long publicationCount() {
    Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM EVENT_PUBLICATION", Long.class);
    return count == null ? 0 : count;
  }

  private void awaitStatus(String status, long expectedCount) throws Exception {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
    while (System.nanoTime() < deadline) {
      Long count =
          jdbcTemplate.queryForObject(
              "SELECT COUNT(*) FROM EVENT_PUBLICATION WHERE STATUS = ?", Long.class, status);
      if (count != null && count == expectedCount) {
        return;
      }
      Thread.sleep(20);
    }
    assertEquals(
        expectedCount,
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM EVENT_PUBLICATION WHERE STATUS = ?", Long.class, status));
  }

  private void insertCompletedPublication(Instant completionDate) {
    jdbcTemplate.update(
        """
        INSERT INTO EVENT_PUBLICATION (
          ID, LISTENER_ID, EVENT_TYPE, SERIALIZED_EVENT, PUBLICATION_DATE,
          COMPLETION_DATE, STATUS, COMPLETION_ATTEMPTS, LAST_RESUBMISSION_DATE
        ) VALUES (?, ?, ?, ?, ?, ?, 'COMPLETED', 1, NULL)
        """,
        UUID.randomUUID().toString(),
        "test-listener",
        DisplayDeletedEvent.class.getName(),
        "{}",
        Timestamp.from(completionDate.minusSeconds(1)),
        Timestamp.from(completionDate));
  }
}
