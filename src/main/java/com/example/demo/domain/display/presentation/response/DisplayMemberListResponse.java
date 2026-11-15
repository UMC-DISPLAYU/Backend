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
      @Schema(example = "MANAGER") String role,
      @Schema(description = "사용자의 기본 닉네임", example = "디유") String nickname,
      @Schema(description = "대기 중인 초대 ID. 수락 완료 팀원은 null", nullable = true) Long invitationId) {}
}
