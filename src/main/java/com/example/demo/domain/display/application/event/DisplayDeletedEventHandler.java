package com.example.demo.domain.display.application.event;

import com.example.demo.domain.display.application.command.CleanupDeletedDisplayService;
import com.example.demo.domain.display.contract.event.v1.DisplayDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class DisplayDeletedEventHandler {

  private static final Logger log = LoggerFactory.getLogger(DisplayDeletedEventHandler.class);

  private final CleanupDeletedDisplayService cleanupDeletedDisplayService;

  public DisplayDeletedEventHandler(CleanupDeletedDisplayService cleanupDeletedDisplayService) {
    this.cleanupDeletedDisplayService = cleanupDeletedDisplayService;
  }

  @ApplicationModuleListener
  public void handle(DisplayDeletedEvent event) {
    log.info(
        "Domain event received. eventType=DisplayDeletedEvent eventId={} listenerId={} displayId={}",
        event.eventId(),
        getClass().getSimpleName(),
        event.displayId());

    try {
      cleanupDeletedDisplayService.cleanup(event.displayId());
      logAfterCommit(event);
    } catch (RuntimeException exception) {
      log.warn(
          "Domain event failed. eventType=DisplayDeletedEvent eventId={} listenerId={} displayId={}",
          event.eventId(),
          getClass().getSimpleName(),
          event.displayId(),
          exception);
      throw exception;
    }
  }

  private void logAfterCommit(DisplayDeletedEvent event) {
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            log.info(
                "Domain event subscriber transaction committed. eventType=DisplayDeletedEvent eventId={} listenerId={} displayId={}",
                event.eventId(),
                DisplayDeletedEventHandler.class.getSimpleName(),
                event.displayId());
          }
        });
  }
}
