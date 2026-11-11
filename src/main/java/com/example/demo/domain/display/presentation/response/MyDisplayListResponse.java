package com.example.demo.domain.display.presentation.response;

import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record MyDisplayListResponse(
    List<MyDisplayResponse> createdDisplays, List<MyDisplayResponse> participatedDisplays) {

  public record MyDisplayResponse(
      Long displayId,
      String title,
      String displayStatus,
      DisplayStatus publishStatus,
      LocalDate startDate,
      LocalDate endDate,
      String school,
      String department,
      String placeName,
      String postImageUrl,
      boolean isLeader,
      @Schema(
              description = "해당 전시에서 조회 사용자의 역할 (대표자: TEAM_LEADER, 관리자: MANAGER, 팀원: TEAM_MEM)",
              allowableValues = {"TEAM_LEADER", "MANAGER", "TEAM_MEM"},
              example = "TEAM_LEADER")
          TeamMemberRole role) {}
}
