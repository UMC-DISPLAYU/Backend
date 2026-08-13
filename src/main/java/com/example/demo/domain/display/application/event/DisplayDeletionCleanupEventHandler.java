package com.example.demo.domain.display.application.event;

import com.example.demo.domain.display.application.port.DisplayDeletionCleanupFailureRecorder;
import com.example.demo.domain.display.application.port.DisplayDeletionCleanupPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class DisplayDeletionCleanupEventHandler {

  private static final Logger log =
      LoggerFactory.getLogger(DisplayDeletionCleanupEventHandler.class);
  private static final int MAX_RETRY_COUNT = 3;

  private final DisplayDeletionCleanupPort displayDeletionCleanupPort;
  private final DisplayDeletionCleanupFailureRecorder failureRecorder;

  public DisplayDeletionCleanupEventHandler(
      DisplayDeletionCleanupPort displayDeletionCleanupPort,
      DisplayDeletionCleanupFailureRecorder failureRecorder) {
    this.displayDeletionCleanupPort = displayDeletionCleanupPort;
    this.failureRecorder = failureRecorder;
  }

  @Async("displayDeletionCleanupExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(DisplayDeletedEvent event) {
    RuntimeException lastException = null;
    for (int attempt = 1; attempt <= MAX_RETRY_COUNT; attempt++) {
      try {
        displayDeletionCleanupPort.cleanupDisplayChildren(event.displayId(), event.deletedAt());
        return;
      } catch (RuntimeException e) {
        lastException = e;
        log.warn(
            "Failed to cleanup deleted display. displayId={} attempt={}/{}",
            event.displayId(),
            attempt,
            MAX_RETRY_COUNT,
            e);
      }
    }
    failureRecorder.recordFailure(
        event.displayId(), event.deletedAt(), MAX_RETRY_COUNT, lastException);
  }
}
