package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.application.port.DisplayDeletionCleanupPort;
import com.example.demo.domain.display.application.port.DisplayListCacheEvictionPort;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.global.error.BusinessException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteDisplayService {

  private final DisplayRepository displayRepository;
  private final DisplayDeletionCleanupPort displayDeletionCleanupPort;
  private final DisplayListCacheEvictionPort displayListCacheEvictionPort;
  private final DisplayPermissionChecker displayPermissionChecker;
  private final Clock clock;

  public DeleteDisplayService(
      DisplayRepository displayRepository,
      DisplayDeletionCleanupPort displayDeletionCleanupPort,
      DisplayListCacheEvictionPort displayListCacheEvictionPort,
      DisplayPermissionChecker displayPermissionChecker,
      Clock clock) {
    this.displayRepository = displayRepository;
    this.displayDeletionCleanupPort = displayDeletionCleanupPort;
    this.displayListCacheEvictionPort = displayListCacheEvictionPort;
    this.displayPermissionChecker = displayPermissionChecker;
    this.clock = clock;
  }

  @Transactional
  public void deleteDisplay(DeleteDisplayCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display =
        displayRepository
            .findById(command.displayId())
            .filter(candidate -> !candidate.isDeleted())
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));
    displayPermissionChecker.requireTeamLeader(display, command.userId());

    LocalDateTime deletedAt = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    displayDeletionCleanupPort.cleanupDisplayChildren(display.getId(), deletedAt);
    display.delete();
    displayListCacheEvictionPort.evictAfterCommit();
  }
}
