package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record GraduationDisplayResult(List<ExhibitionResult> exhibitions) {

  public record ExhibitionResult(
      Long displayId,
      String title,
      String posterImageUrl,
      LocalDate startedAt,
      LocalDate endedAt,
      long dayLeft) {

    public static ExhibitionResult from(
        ClosingSoonDisplayQueryResult queryResult, LocalDate today) {
      return new ExhibitionResult(
          queryResult.displayId(),
          queryResult.title(),
          queryResult.posterImageUrl(),
          queryResult.startedAt(),
          queryResult.endedAt(),
          ChronoUnit.DAYS.between(today, queryResult.endedAt()));
    }
  }
}
