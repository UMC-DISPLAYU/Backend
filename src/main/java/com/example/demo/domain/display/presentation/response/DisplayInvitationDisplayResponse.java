package com.example.demo.domain.display.presentation.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record DisplayInvitationDisplayResponse(List<ExhibitionResponse> exhibitions) {

  public record ExhibitionResponse(
      Long displayId,
      String title,
      String posterImageUrl,
      String schoolDepartmentName,
      LocalDate startedAt,
      LocalDate endedAt,
      long dayLeft,
      @JsonProperty("isArchived") boolean isArchived,
      @Schema(
              types = {"object", "null"},
              description = "대표 포스터 표시 범위. null이면 조정 없음.")
          PosterCropResponse posterCrop) {}
}
