package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.TeamMemberRepository;
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

  public ExitDisplayService(
      TeamMemberRepository teamMemberRepository,
      Clock clock,
      DisplayPermissionChecker displayPermissionChecker) {
    this.teamMemberRepository = teamMemberRepository;
    this.clock = clock;
    this.displayPermissionChecker = displayPermissionChecker;
  }

  @Transactional
  public void exit(Long displayId, Long userId) {
    Objects.requireNonNull(displayId, "displayId must not be null.");
    Objects.requireNonNull(userId, "userId must not be null.");

    TeamMember teamMember =
        teamMemberRepository
            .findActiveAcceptedByDisplayIdAndUserId(displayId, userId)
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_MEMBER_NOT_FOUND));
    displayPermissionChecker.requireExitAllowed(teamMember);

    teamMember.softDelete(LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
  }
}
