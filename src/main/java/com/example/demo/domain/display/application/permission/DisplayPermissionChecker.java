package com.example.demo.domain.display.application.permission;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import org.springframework.stereotype.Component;

@Component
public class DisplayPermissionChecker {

  public void requireTeamLeader(Display display, Long userId) {
    if (!display.isTeamLeader(userId)) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }
  }

  public void requireAcceptedTeamMember(Display display, Long userId) {
    if (!display.hasAcceptedTeamMember(userId)) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }
  }

  public void requireContentEditor(Display display, Long userId) {
    // 전시 콘텐츠는 업로더와 관계없이 대표자와 관리자만 편집할 수 있다.
    if (!display.isOwner(userId) && !display.isTeamLeader(userId) && !display.isManager(userId)) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_CONTENT_PERMISSION_DENIED);
    }
  }

  public void requireInvitationManager(Display display, Long userId) {
    if (!display.canInviteMember(userId)) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_INVITATION_PERMISSION_DENIED);
    }
  }

  public void requireInvitationTokenManager(Display display, Long userId) {
    if (!display.isTeamLeader(userId)) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_INVITATION_PERMISSION_DENIED);
    }
  }

  public void requireExitAllowed(TeamMember teamMember) {
    if (teamMember.getRole() == TeamMemberRole.TEAM_LEADER) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }
  }
}
