package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.entity.TeamMember;

public record DisplayMemberRoleResult(Long displayId, Long teamMemberId, Long userId, String role) {

  public static DisplayMemberRoleResult from(Long displayId, TeamMember member) {
    return new DisplayMemberRoleResult(
        displayId, member.getId(), member.getUserId().value(), member.getRole().name());
  }
}
