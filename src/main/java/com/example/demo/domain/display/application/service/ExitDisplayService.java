package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.repository.TeamMemberRepository;
import com.example.demo.domain.displayartwork.application.command.DisplayArtworkCleanupService;
import com.example.demo.global.error.BusinessException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExitDisplayService {

  private final TeamMemberRepository teamMemberRepository;
  private final Clock clock;
  private final DisplayPermissionChecker displayPermissionChecker;
  private final DisplayRepository displayRepository;
  private final DisplayArtworkCleanupService artworkCleanupService;

  public ExitDisplayService(
      TeamMemberRepository teamMemberRepository,
      Clock clock,
      DisplayPermissionChecker displayPermissionChecker,
      DisplayRepository displayRepository,
      DisplayArtworkCleanupService artworkCleanupService) {
    this.teamMemberRepository = teamMemberRepository;
    this.clock = clock;
    this.displayPermissionChecker = displayPermissionChecker;
    this.displayRepository = displayRepository;
    this.artworkCleanupService = artworkCleanupService;
  }

  @Transactional
  public void exit(Long displayId, Long userId) {
    Objects.requireNonNull(displayId, "displayId must not be null.");
    Objects.requireNonNull(userId, "userId must not be null.");

    displayRepository
        .findById(displayId)
        .filter(display -> !display.isDeleted())
        .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));

    TeamMember teamMember =
        teamMemberRepository
            .findActiveAcceptedByDisplayIdAndUserId(displayId, userId)
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_MEMBER_NOT_FOUND));
    displayPermissionChecker.requireExitAllowed(teamMember);

    // 내보내기와 같은 기준으로, 나가는 사용자가 작가로 연결된 작품을 먼저 함께 정리한다.
    artworkCleanupService.deleteByArtistAccount(displayId, userId);
    teamMember.softDelete(LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
  }
}
