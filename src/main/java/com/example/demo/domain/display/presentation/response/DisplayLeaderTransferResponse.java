package com.example.demo.domain.display.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record DisplayLeaderTransferResponse(
    @Schema(example = "1") Long displayId,
    MemberResponse previousLeader,
    MemberResponse newLeader) {

  public record MemberResponse(
      @Schema(example = "10") Long teamMemberId,
      @Schema(example = "101") Long userId,
      @Schema(example = "MANAGER") String role) {}
}
