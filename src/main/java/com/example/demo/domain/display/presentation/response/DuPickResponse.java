package com.example.demo.domain.display.presentation.response;

import java.time.LocalDate;
import java.util.List;

public record DuPickResponse(
    List<DuPickItemResponse> duPicks, CursorPaginationResponse pagination) {

  public record DuPickItemResponse(
      Long duPickId,
      String title,
      String subtitle,
      String bannerImageUrl,
      String authorName,
      LocalDate createdAt) {}

  public record CursorPaginationResponse(Long nextCursor, int size, boolean hasNext) {}
}
