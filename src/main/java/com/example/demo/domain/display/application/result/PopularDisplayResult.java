package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record PopularDisplayResult(List<ExhibitionResult> exhibitions) {

  public record ExhibitionResult(
      Long displayId,
      String title,
      String posterImageUrl,
      String organization,
      String department,
      LocalDate startedAt,
      LocalDate endedAt,
      long dayLeft,
      boolean isArchived,
      String placeName) {

    public static ExhibitionResult from(
        ClosingSoonDisplayQueryResult queryResult, LocalDate today) {
      return new ExhibitionResult(
          queryResult.displayId(),
          queryResult.title(),
          queryResult.posterImageUrl(),
          queryResult.organization(),
          queryResult.department(),
          queryResult.startedAt(),
          queryResult.endedAt(),
          ChronoUnit.DAYS.between(today, queryResult.endedAt()),
          false,
          queryResult.placeName());
    }

    public ExhibitionResult withArchived(boolean isArchived) {
      return new ExhibitionResult(
          displayId,
          title,
          posterImageUrl,
          organization,
          department,
          startedAt,
          endedAt,
          dayLeft,
          isArchived,
          placeName);
    }
  }
}
