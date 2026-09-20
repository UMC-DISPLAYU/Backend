package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.application.query.DisplayScreeningSummaryQueryResult;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public record DisplayScreeningSummaryResult(
    Long displayId,
    String title,
    Long requesterId,
    String status,
    Instant requestedAt,
    String school,
    String leaderName,
    LocalDate startDate,
    LocalDate endDate,
    String posterImageUrl) {

  public static DisplayScreeningSummaryResult from(DisplayScreeningSummaryQueryResult result) {
    return new DisplayScreeningSummaryResult(
        result.displayId(),
        result.title(),
        result.requesterId(),
        result.status().name(),
        result.requestedAt() == null ? null : result.requestedAt().toInstant(ZoneOffset.UTC),
        result.school(),
        result.leaderName(),
        result.startDate(),
        result.endDate(),
        result.posterImageUrl());
  }
}
