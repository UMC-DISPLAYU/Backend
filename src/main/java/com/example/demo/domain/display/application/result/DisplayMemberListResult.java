package com.example.demo.domain.display.application.result;

import java.util.List;

public record DisplayMemberListResult(
    Long displayId, List<TeamMemberResult> memberAccept, List<TeamMemberResult> memberPending) {

  public record TeamMemberResult(
      Long teamMemberId,
      Long userId,
      String displayNickname,
      boolean loggedIn,
      boolean artistVerified,
      boolean accepted,
      String role) {}
}
