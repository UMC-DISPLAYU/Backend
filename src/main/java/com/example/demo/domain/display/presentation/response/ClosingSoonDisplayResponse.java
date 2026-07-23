package com.example.demo.domain.display.presentation.response;

import java.time.LocalDate;
import java.util.List;

public record ClosingSoonDisplayResponse(
    List<ExhibitionResponse> exhibitions, CursorPaginationResponse pagination) {

  public record ExhibitionResponse(
      Long displayId,
      String title,
      String posterImageUrl,
      String organization,
      String department,
      LocalDate startedAt,
      LocalDate endedAt,
      long dayLeft) {}

  public record CursorPaginationResponse(String nextCursor, int size, boolean hasNext) {}
}
