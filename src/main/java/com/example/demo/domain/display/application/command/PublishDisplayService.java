package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.application.port.DisplayListCacheEvictionPort;
import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.application.service.DisplayContentPublicationService;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayLikeRepository;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublishDisplayService {

  private final DisplayRepository displayRepository;
  private final DisplayLikeRepository displayLikeRepository;
  private final DisplayListCacheEvictionPort displayListCacheEvictionPort;
  private final DisplayContentPublicationService displayContentPublicationService;
  private final DisplayPermissionChecker displayPermissionChecker;

  public PublishDisplayService(
      DisplayRepository displayRepository,
      DisplayLikeRepository displayLikeRepository,
      DisplayListCacheEvictionPort displayListCacheEvictionPort,
      DisplayContentPublicationService displayContentPublicationService,
      DisplayPermissionChecker displayPermissionChecker) {
    this.displayRepository = displayRepository;
    this.displayLikeRepository = displayLikeRepository;
    this.displayListCacheEvictionPort = displayListCacheEvictionPort;
    this.displayContentPublicationService = displayContentPublicationService;
    this.displayPermissionChecker = displayPermissionChecker;
  }

  @Transactional
  public DisplayDetailResult publishDisplay(PublishDisplayCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display =
        displayRepository
            .findById(command.displayId())
            .filter(candidate -> !candidate.isDeleted())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
    displayPermissionChecker.requireTeamLeader(display, command.userId());

    if (display.isPublished()) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_ALREADY_PUBLISHED);
    }

    display.publish();
    displayContentPublicationService.publishForDisplay(display.getId());
    displayListCacheEvictionPort.evictAfterCommit();
    return DisplayDetailResult.from(
        display, displayLikeRepository.countByDisplayId(display.getId()));
  }
}
