package com.example.demo.domain.display.application.port;

import java.time.LocalDateTime;

public interface DisplayDeletionCleanupPort {

  void cleanupDisplayChildren(Long displayId, LocalDateTime deletedAt);
}
