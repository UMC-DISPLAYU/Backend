package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.application.query.DisplaySummaryQueryResult;
import java.time.LocalDate;

public record DisplaySummaryResult(
    Long displayId,
    String title,
    String organization,
    String department,
    String placeName,
    LocalDate startDate,
    LocalDate endDate,
    String posterImageUrl) {

  public static DisplaySummaryResult from(DisplaySummaryQueryResult queryResult) {
    return new DisplaySummaryResult(
        queryResult.displayId(),
        queryResult.title(),
        queryResult.organization(),
        queryResult.department(),
        queryResult.placeName(),
        queryResult.startDate(),
        queryResult.endDate(),
        queryResult.posterImageUrl());
  }
}
