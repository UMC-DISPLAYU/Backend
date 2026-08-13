package com.example.demo.domain.display.application.port;

import java.time.LocalDateTime;

public interface DisplayDeletionCleanupFailureRecorder {

  void recordFailure(
      Long displayId, LocalDateTime deletedAt, int retryCount, RuntimeException exception);
}
