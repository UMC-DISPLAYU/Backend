package com.example.demo.domain.display.infrastructure.event;

import com.example.demo.domain.display.infrastructure.persistence.DisplayDeletionCleanupFailure;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayDeletionCleanupFailureJpaRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class LegacyDisplayDeletionCleanupRecoveryRunner implements ApplicationRunner {

  private static final Logger log =
      LoggerFactory.getLogger(LegacyDisplayDeletionCleanupRecoveryRunner.class);

  private final SpringDataDisplayDeletionCleanupFailureJpaRepository repository;
  private final LegacyDisplayDeletionCleanupRecoveryProcessor processor;

  public LegacyDisplayDeletionCleanupRecoveryRunner(
      SpringDataDisplayDeletionCleanupFailureJpaRepository repository,
      LegacyDisplayDeletionCleanupRecoveryProcessor processor) {
    this.repository = repository;
    this.processor = processor;
  }

  @Override
  public void run(ApplicationArguments arguments) {
    long cursor = 0L;
    while (true) {
      List<DisplayDeletionCleanupFailure> failures =
          repository.findTop100ByRecoveredAtIsNullAndIdGreaterThanOrderByIdAsc(cursor);
      if (failures.isEmpty()) {
        return;
      }

      for (DisplayDeletionCleanupFailure failure : failures) {
        cursor = failure.getId();
        try {
          processor.recover(failure.getId());
        } catch (RuntimeException exception) {
          log.error(
              "Failed to migrate legacy display cleanup. failureId={} displayId={}",
              failure.getId(),
              failure.getDisplayId(),
              exception);
        }
      }
    }
  }
}
