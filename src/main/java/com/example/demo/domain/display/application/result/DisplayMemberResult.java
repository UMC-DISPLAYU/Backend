package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.user.domain.aggregate.User;

public record DisplayMemberResult(
    Long teamMemberId,
    Long userId,
    String displayNickname,
    boolean loggedIn,
    boolean artistVerified,
    boolean accepted,
    String role) {

  public static DisplayMemberResult from(TeamMember teamMember, User user) {
    return new DisplayMemberResult(
        teamMember.getId(),
        teamMember.getUserId().value(),
        teamMember.getDisplayNickname(),
        user != null && user.getDeletedAt() == null,
        user != null && user.isVerified(),
        teamMember.isAccepted(),
        teamMember.getRole().name());
  }
}
