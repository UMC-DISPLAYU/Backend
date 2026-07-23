package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.entity.TeamMember;
import java.util.List;

public record DisplayMemberListResult(Long displayId, List<TeamMemberResult> members) {

  public static DisplayMemberListResult of(Long displayId, List<TeamMember> teamMembers) {
    return new DisplayMemberListResult(
        displayId,
        teamMembers.stream().filter(TeamMember::isAccepted).map(TeamMemberResult::from).toList());
  }

  public record TeamMemberResult(
      Long teamMemberId, Long userId, String displayNickname, String role) {

    private static TeamMemberResult from(TeamMember teamMember) {
      return new TeamMemberResult(
          teamMember.getId(),
          teamMember.getUserId().value(),
          teamMember.getDisplayNickname(),
          teamMember.getRole().name());
    }
  }
}
