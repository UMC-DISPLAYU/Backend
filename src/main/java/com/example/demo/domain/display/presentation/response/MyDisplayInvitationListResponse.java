package com.example.demo.domain.display.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record MyDisplayInvitationListResponse(List<InvitationResponse> invitations) {

  @Schema(name = "MyDisplayInvitation")
  public record InvitationResponse(
      Long invitationId,
      Long displayId,
      String thumbnailUrl,
      LocalDate startDate,
      LocalDate endDate,
      String location,
      String userNickname,
      String leaderName,
      String title,
      String schoolDepartmentName,
      String placeName,
      @Schema(
              types = {"object", "null"},
              description = "대표 포스터 표시 범위. null이면 조정 없음.")
          PosterCropResponse posterCrop) {}
}
