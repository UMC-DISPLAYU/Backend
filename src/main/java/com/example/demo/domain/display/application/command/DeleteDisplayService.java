package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.application.event.DisplayDeletedEvent;
import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.application.port.DisplayListCacheEvictionPort;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.global.error.BusinessException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteDisplayService {

  private final DisplayRepository displayRepository;
  private final DisplayListCacheEvictionPort displayListCacheEvictionPort;
  private final DisplayPermissionChecker displayPermissionChecker;
  private final ApplicationEventPublisher eventPublisher;
  private final Clock clock;

  public DeleteDisplayService(
      DisplayRepository displayRepository,
      DisplayListCacheEvictionPort displayListCacheEvictionPort,
      DisplayPermissionChecker displayPermissionChecker,
      ApplicationEventPublisher eventPublisher,
      Clock clock) {
    this.displayRepository = displayRepository;
    this.displayListCacheEvictionPort = displayListCacheEvictionPort;
    this.displayPermissionChecker = displayPermissionChecker;
    this.eventPublisher = eventPublisher;
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
    display.delete();
    displayListCacheEvictionPort.evictAfterCommit();
    eventPublisher.publishEvent(new DisplayDeletedEvent(display.getId(), deletedAt));
  }
}
