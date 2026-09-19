package com.example.demo.domain.display.application.event;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.example.demo.domain.display.application.command.CleanupDeletedDisplayService;
import com.example.demo.domain.display.contract.event.v1.DisplayDeletedEvent;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DisplayDeletedEventHandlerTest {

  private final CleanupDeletedDisplayService cleanupService =
      org.mockito.Mockito.mock(CleanupDeletedDisplayService.class);
  private final DisplayDeletedEventHandler handler = new DisplayDeletedEventHandler(cleanupService);

  @Test
  void propagatesFailureSoTheRegistryCanRetryThisSubscriber() {
    DisplayDeletedEvent event =
        new DisplayDeletedEvent(UUID.randomUUID(), 10L, LocalDateTime.of(2026, 9, 19, 12, 0));
    RuntimeException failure = new IllegalStateException("display cleanup failed");
    doThrow(failure).when(cleanupService).cleanup(event.displayId());

    assertThrows(RuntimeException.class, () -> handler.handle(event));

    verify(cleanupService).cleanup(event.displayId());
  }
}
