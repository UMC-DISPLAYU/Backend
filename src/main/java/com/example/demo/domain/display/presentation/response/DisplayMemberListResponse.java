package com.example.demo.domain.display.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record DisplayMemberListResponse(
    @Schema(example = "1") Long displayId,
    List<TeamMemberResponse> memberAccept,
    List<TeamMemberResponse> memberPending) {

  public record TeamMemberResponse(
      @Schema(example = "11", nullable = true) Long teamMemberId,
      @Schema(example = "102") Long userId,
      @Schema(example = "디유 작가") String displayNickname,
      @Schema(example = "true") boolean loggedIn,
      @Schema(example = "true") boolean artistVerified,
      @Schema(example = "true") boolean accepted,
      @Schema(example = "MANAGER") String role) {}
}
