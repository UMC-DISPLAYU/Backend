package com.example.demo.domain.display.infrastructure.event;

import com.example.demo.domain.display.contract.event.v1.DisplayDeletedEvent;
import com.example.demo.domain.display.infrastructure.persistence.DisplayDeletionCleanupFailure;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayDeletionCleanupFailureJpaRepository;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LegacyDisplayDeletionCleanupRecoveryProcessor {

  private static final String EVENT_ID_PREFIX = "legacy-display-deletion-cleanup-failure:";

  private final SpringDataDisplayDeletionCleanupFailureJpaRepository repository;
  private final ApplicationEventPublisher eventPublisher;
  private final Clock clock;

  public LegacyDisplayDeletionCleanupRecoveryProcessor(
      SpringDataDisplayDeletionCleanupFailureJpaRepository repository,
      ApplicationEventPublisher eventPublisher,
      Clock clock) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public void recover(Long failureId) {
    DisplayDeletionCleanupFailure failure =
        repository
            .findById(failureId)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Display deletion cleanup failure not found: " + failureId));
    if (failure.getRecoveredAt() != null) {
      return;
    }

    UUID eventId =
        UUID.nameUUIDFromBytes((EVENT_ID_PREFIX + failureId).getBytes(StandardCharsets.UTF_8));
    eventPublisher.publishEvent(
        new DisplayDeletedEvent(eventId, failure.getDisplayId(), failure.getDeletedAt()));
    failure.markRecovered(LocalDateTime.now(clock));
  }
}
