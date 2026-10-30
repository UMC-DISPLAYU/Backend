package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.entity.DisplayScreening;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public record DisplayScreeningHistoryResult(
    Long screeningId,
    String status,
    Instant requestedAt,
    Instant processedAt,
    Long reviewerId,
    String rejectionReason) {

  public static DisplayScreeningHistoryResult from(DisplayScreening screening) {
    return new DisplayScreeningHistoryResult(
        screening.getId(),
        screening.getStatus().name(),
        toInstant(screening.getRequestedAt()),
        toInstant(screening.getProcessedAt()),
        screening.getReviewerId(),
        screening.getRejectionReason());
  }

  private static Instant toInstant(LocalDateTime value) {
    return value == null ? null : value.toInstant(ZoneOffset.UTC);
  }
}
