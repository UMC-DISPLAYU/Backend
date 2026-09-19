package com.example.demo.domain.display.application.event;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.example.demo.domain.display.application.port.DisplayDeletionCleanupPort;
import com.example.demo.domain.display.contract.event.v1.DisplayDeletedEvent;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DisplayDeletionCleanupEventHandlerTest {

  private final DisplayDeletionCleanupPort cleanupPort =
      org.mockito.Mockito.mock(DisplayDeletionCleanupPort.class);
  private final DisplayDeletionCleanupEventHandler handler =
      new DisplayDeletionCleanupEventHandler(cleanupPort);

  @Test
  void propagatesCleanupFailureForRegistryRecovery() {
    DisplayDeletedEvent event =
        new DisplayDeletedEvent(UUID.randomUUID(), 10L, LocalDateTime.of(2026, 8, 13, 12, 0));
    RuntimeException exception = new RuntimeException("cleanup failed");
    doThrow(exception)
        .when(cleanupPort)
        .cleanupDisplayChildren(event.displayId(), event.deletedAt());

    assertThrows(RuntimeException.class, () -> handler.handle(event));

    verify(cleanupPort).cleanupDisplayChildren(event.displayId(), event.deletedAt());
    verifyNoMoreInteractions(cleanupPort);
  }

  @Test
  void delegatesCleanupOnce() {
    DisplayDeletedEvent event =
        new DisplayDeletedEvent(UUID.randomUUID(), 10L, LocalDateTime.of(2026, 8, 13, 12, 0));

    handler.handle(event);

    verify(cleanupPort).cleanupDisplayChildren(event.displayId(), event.deletedAt());
  }
}
