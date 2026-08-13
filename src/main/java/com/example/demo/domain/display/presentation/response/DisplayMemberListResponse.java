package com.example.demo.domain.display.presentation.response;

import java.util.List;

public record DisplayMemberListResponse(
    Long displayId, List<TeamMemberResponse> memberAccept, List<TeamMemberResponse> memberPending) {

  public record TeamMemberResponse(
      Long teamMemberId,
      Long userId,
      String displayNickname,
      boolean loggedIn,
      boolean artistVerified,
      boolean accepted,
      String role) {}
}
