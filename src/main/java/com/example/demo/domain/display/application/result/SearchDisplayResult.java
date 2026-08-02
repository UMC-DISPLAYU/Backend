package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.application.query.SearchDisplayQueryResult;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record SearchDisplayResult(List<ExhibitionResult> exhibitions, PaginationResult pagination) {

  public record ExhibitionResult(
      Long displayId,
      String title,
      String posterImageUrl,
      LocalDate startedAt,
      LocalDate endedAt,
      long dayLeft,
      boolean isBookmarked) {

    public static ExhibitionResult from(SearchDisplayQueryResult queryResult, LocalDate today) {
      return new ExhibitionResult(
          queryResult.displayId(),
          queryResult.title(),
          queryResult.posterImageUrl(),
          queryResult.startedAt(),
          queryResult.endedAt(),
          ChronoUnit.DAYS.between(today, queryResult.endedAt()),
          false);
    }

    public ExhibitionResult withBookmarked(boolean isBookmarked) {
      return new ExhibitionResult(
          displayId, title, posterImageUrl, startedAt, endedAt, dayLeft, isBookmarked);
    }
  }

  public record PaginationResult(Long nextCursor, int size, boolean hasNext) {}
}
