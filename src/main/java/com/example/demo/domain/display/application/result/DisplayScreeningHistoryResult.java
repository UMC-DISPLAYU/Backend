package com.example.demo.domain.display.application.result;

import static com.example.demo.domain.display.application.result.DisplayScreeningDetailResult.toInstant;

import com.example.demo.domain.display.domain.entity.DisplayScreening;
import java.time.Instant;

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
        toInstant(screening.getRequestedAt()),
        toInstant(screening.getProcessedAt()),
        screening.getReviewerId(),
        screening.getRejectionReason(),
        screening.getCancelledBy());
  }
}
