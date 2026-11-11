package com.example.demo.domain.display.presentation.response;

import com.example.demo.domain.display.domain.type.DisplayStatus;
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
              types = {"object", "null"},
              description = "대표 포스터 표시 범위. null이면 조정 없음.")
          PosterCropResponse posterCrop) {}
}
