package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.entity.DisplayScreening;
import java.time.Instant;
import java.time.ZoneOffset;

public record DisplayScreeningHistoryResult(
    Long screeningId,
    String status,
    Long requesterId,
    Instant requestedAt,
    Instant processedAt,
    Long reviewerId,
    String rejectionReason,
    Long cancelledBy) {
  public static DisplayScreeningHistoryResult from(DisplayScreening screening) {
    return new DisplayScreeningHistoryResult(
        screening.getId(),
        screening.getStatus().name(),
        screening.getRequesterId(),
        screening.getRequestedAt().toInstant(ZoneOffset.UTC),
        screening.getProcessedAt() == null
            ? null
            : screening.getProcessedAt().toInstant(ZoneOffset.UTC),
        screening.getReviewerId(),
        screening.getRejectionReason(),
        screening.getCancelledBy());
  }
}
