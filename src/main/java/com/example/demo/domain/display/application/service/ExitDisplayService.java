package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.TeamMemberRepository;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExitDisplayService {

  private final TeamMemberRepository teamMemberRepository;
  private final Clock clock;

  public ExitDisplayService(TeamMemberRepository teamMemberRepository, Clock clock) {
    this.teamMemberRepository = teamMemberRepository;
    this.clock = clock;
  }

  @Transactional
  public void exit(Long displayId, Long userId) {
    Objects.requireNonNull(displayId, "displayId must not be null.");
    Objects.requireNonNull(userId, "userId must not be null.");

    TeamMember teamMember =
        teamMemberRepository
            .findActiveAcceptedByDisplayIdAndUserId(displayId, userId)
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_MEMBER_NOT_FOUND));
    if (teamMember.getRole() == TeamMemberRole.TEAM_LEADER) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }

    teamMember.softDelete(LocalDateTime.now(clock));
  }
}
