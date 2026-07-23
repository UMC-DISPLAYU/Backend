package com.example.demo.domain.display.presentation.response;

import java.util.List;

public record DisplayMemberListResponse(Long displayId, List<TeamMemberResponse> members) {

  public record TeamMemberResponse(
      Long teamMemberId, Long userId, String displayNickname, String role) {}
}
