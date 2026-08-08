package com.example.demo.domain.display.presentation.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

public record SearchDisplayResponse(
    List<ExhibitionResponse> exhibitions, CursorPaginationResponse pagination) {

  public record ExhibitionResponse(
      Long displayId,
      String title,
      String posterImageUrl,
      String schoolDepartmentName,
      LocalDate startedAt,
      LocalDate endedAt,
      long dayLeft,
      @JsonProperty("isArchived") boolean isArchived) {}

  public record CursorPaginationResponse(Long nextCursor, int size, boolean hasNext) {}
}
