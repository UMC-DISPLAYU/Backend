package com.example.demo.domain.display.presentation.response;

import java.time.LocalDate;
import java.util.List;

public record MyDisplayInvitationListResponse(List<InvitationResponse> invitations) {

  public record InvitationResponse(
      Long invitationId,
      Long displayId,
      String thumbnailUrl,
      LocalDate startDate,
      LocalDate endDate,
      String location,
      String userNickname,
      String title,
      String schoolDepartmentName,
      String placeName) {}
}
