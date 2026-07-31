package com.example.demo.domain.display.presentation.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
      BigDecimal latitude,
      BigDecimal longitude,
      @JsonProperty("isBookmarked") boolean isBookmarked) {}

  public record CursorPaginationResponse(Long nextCursor, int size, boolean hasNext) {}
}
