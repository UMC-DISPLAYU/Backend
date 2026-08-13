package com.example.demo.domain.display.application.event;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.demo.domain.display.application.port.DisplayDeletionCleanupFailureRecorder;
import com.example.demo.domain.display.application.port.DisplayDeletionCleanupPort;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DisplayDeletionCleanupEventHandlerTest {

  private final DisplayDeletionCleanupPort cleanupPort =
      org.mockito.Mockito.mock(DisplayDeletionCleanupPort.class);
  private final DisplayDeletionCleanupFailureRecorder failureRecorder =
      org.mockito.Mockito.mock(DisplayDeletionCleanupFailureRecorder.class);
  private final DisplayDeletionCleanupEventHandler handler =
      new DisplayDeletionCleanupEventHandler(cleanupPort, failureRecorder);

  @Test
  void retriesCleanupAndRecordsFailureWhenAllAttemptsFail() {
    DisplayDeletedEvent event = new DisplayDeletedEvent(10L, LocalDateTime.of(2026, 8, 13, 12, 0));
    RuntimeException exception = new RuntimeException("cleanup failed");
    doThrow(exception)
        .when(cleanupPort)
        .cleanupDisplayChildren(event.displayId(), event.deletedAt());

    handler.handle(event);

    verify(cleanupPort, org.mockito.Mockito.times(3))
        .cleanupDisplayChildren(event.displayId(), event.deletedAt());
    verify(failureRecorder).recordFailure(event.displayId(), event.deletedAt(), 3, exception);
  }

  @Test
  void doesNotRecordFailureWhenRetrySucceeds() {
    DisplayDeletedEvent event = new DisplayDeletedEvent(10L, LocalDateTime.of(2026, 8, 13, 12, 0));
    RuntimeException exception = new RuntimeException("cleanup failed once");
    doThrow(exception)
        .doNothing()
        .when(cleanupPort)
        .cleanupDisplayChildren(event.displayId(), event.deletedAt());

    handler.handle(event);

    verify(cleanupPort, org.mockito.Mockito.times(2))
        .cleanupDisplayChildren(event.displayId(), event.deletedAt());
    verify(failureRecorder, never())
        .recordFailure(
            org.mockito.Mockito.anyLong(),
            org.mockito.Mockito.any(LocalDateTime.class),
            org.mockito.Mockito.anyInt(),
            org.mockito.Mockito.any(RuntimeException.class));
  }
}
