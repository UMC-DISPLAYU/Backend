package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.application.port.DisplayListCacheEvictionPort;
import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.application.service.DisplayContentPublicationService;
import com.example.demo.domain.display.domain.aggregate.Display;
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

  public PublishDisplayService(
      DisplayRepository displayRepository,
      DisplayLikeRepository displayLikeRepository,
      DisplayListCacheEvictionPort displayListCacheEvictionPort,
      DisplayContentPublicationService displayContentPublicationService) {
    this.displayRepository = displayRepository;
    this.displayLikeRepository = displayLikeRepository;
    this.displayListCacheEvictionPort = displayListCacheEvictionPort;
    this.displayContentPublicationService = displayContentPublicationService;
  }

  @Transactional
  public DisplayDetailResult publishDisplay(PublishDisplayCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display =
        displayRepository
            .findById(command.displayId())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
    if (!display.isTeamLeader(command.userId())) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }

    display.publish();
    displayContentPublicationService.publishForDisplay(display.getId());
    displayListCacheEvictionPort.evictAfterCommit();
    return DisplayDetailResult.from(
        display, displayLikeRepository.countByDisplayIdAndDeletedAtIsNull(display.getId()));
  }
}
