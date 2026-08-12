package com.example.demo.domain.display.application.event;

import com.example.demo.domain.display.application.port.DisplayDeletionCleanupPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class DisplayDeletionCleanupEventHandler {

  private static final Logger log =
      LoggerFactory.getLogger(DisplayDeletionCleanupEventHandler.class);

  private final DisplayDeletionCleanupPort displayDeletionCleanupPort;

  public DisplayDeletionCleanupEventHandler(DisplayDeletionCleanupPort displayDeletionCleanupPort) {
    this.displayDeletionCleanupPort = displayDeletionCleanupPort;
  }

  @Async("displayDeletionCleanupExecutor")
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(DisplayDeletedEvent event) {
    try {
      displayDeletionCleanupPort.cleanupDisplayChildren(event.displayId(), event.deletedAt());
    } catch (RuntimeException e) {
      log.error("Failed to cleanup deleted display. displayId={}", event.displayId(), e);
      throw e;
    }
  }
}
