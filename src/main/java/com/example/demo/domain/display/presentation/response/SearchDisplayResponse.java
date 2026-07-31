package com.example.demo.domain.display.presentation.response;

import java.time.LocalDate;
import java.util.List;

public record SearchDisplayResponse(
    List<ExhibitionResponse> exhibitions, CursorPaginationResponse pagination) {

  public record ExhibitionResponse(
      Long displayId,
      String title,
      String posterImageUrl,
      LocalDate startedAt,
      LocalDate endedAt,
      long dayLeft,
      boolean isBookmarked) {}

  public record CursorPaginationResponse(Long nextCursor, int size, boolean hasNext) {}
}
