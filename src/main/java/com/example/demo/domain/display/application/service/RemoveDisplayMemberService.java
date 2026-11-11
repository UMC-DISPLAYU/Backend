package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.application.result.RemoveDisplayMemberResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.displayartwork.application.command.DisplayArtworkCleanupService;
import com.example.demo.global.error.BusinessException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveDisplayMemberService {

  private final DisplayRepository displayRepository;
  private final DisplayPermissionChecker permissionChecker;
  private final DisplayArtworkCleanupService artworkCleanupService;
  private final Clock clock;

  public RemoveDisplayMemberService(
      DisplayRepository displayRepository,
      DisplayPermissionChecker permissionChecker,
      DisplayArtworkCleanupService artworkCleanupService,
      Clock clock) {
    this.displayRepository = displayRepository;
    this.permissionChecker = permissionChecker;
    this.artworkCleanupService = artworkCleanupService;
    this.clock = clock;
  }

  @Transactional
  public RemoveDisplayMemberResult remove(Long requesterUserId, Long displayId, Long memberId) {
    Display display = findDisplay(displayId);
    permissionChecker.requireTeamLeader(display, requesterUserId);

    LocalDateTime removedAt = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    TeamMember removedMember = display.removeAcceptedMember(memberId, removedAt);
    Long removedUserId = removedMember.getUserId().value();
    int deletedArtworkCount = artworkCleanupService.deleteByArtistAccount(displayId, removedUserId);

    return new RemoveDisplayMemberResult(
        displayId, removedMember.getId(), removedUserId, deletedArtworkCount);
  }

  private Display findDisplay(Long displayId) {
    return displayRepository
        .findByIdWithOptimisticLock(displayId)
        .filter(display -> !display.isDeleted())
        .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));
  }
}
