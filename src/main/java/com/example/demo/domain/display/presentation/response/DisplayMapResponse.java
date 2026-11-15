package com.example.demo.domain.display.presentation.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DisplayMapResponse(
    List<MarkerResponse> markers, CursorPaginationResponse pagination) {

  public record MarkerResponse(
      Long displayId,
      String title,
      LocalDate startDate,
      LocalDate endDate,
      String locationName,
      String posterImageUrl,
      String schoolDepartmentName,
      BigDecimal latitude,
      BigDecimal longitude,
      @JsonProperty("isArchived") boolean isArchived,
      @Schema(
              types = {"object", "null"},
              description = "대표 포스터 표시 범위. null이면 조정 없음.")
          PosterCropResponse posterCrop) {}

  public record CursorPaginationResponse(Long nextCursor, int size, boolean hasNext) {}
}
