package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.entity.TeamMember;

public record DisplayMemberResult(
    Long teamMemberId, Long userId, String displayNickname, String role) {

  public static DisplayMemberResult from(TeamMember teamMember) {
    return new DisplayMemberResult(
        teamMember.getId(),
        teamMember.getUserId().value(),
        teamMember.getDisplayNickname(),
        teamMember.getRole().name());
  }
}
