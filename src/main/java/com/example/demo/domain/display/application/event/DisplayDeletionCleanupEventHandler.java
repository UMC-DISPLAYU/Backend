package com.example.demo.domain.display.application.event;

import com.example.demo.domain.display.application.port.DisplayDeletionCleanupPort;
import com.example.demo.domain.display.contract.event.v1.DisplayDeletedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class DisplayDeletionCleanupEventHandler {

  private final DisplayDeletionCleanupPort displayDeletionCleanupPort;

  public DisplayDeletionCleanupEventHandler(DisplayDeletionCleanupPort displayDeletionCleanupPort) {
    this.displayDeletionCleanupPort = displayDeletionCleanupPort;
  }

  @ApplicationModuleListener
  public void handle(DisplayDeletedEvent event) {
    displayDeletionCleanupPort.cleanupDisplayChildren(event.displayId(), event.deletedAt());
  }
}
