package com.example.demo.domain.admin.presentation.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class ReviewResponses {
  private ReviewResponses() {}

  public record Summary(
      Long displayId, String title, Long requesterId, String status, Instant requestedAt) {}

  public record Page(List<Summary> items, Long nextCursor, boolean hasNext) {}

  public record Detail(
      Long displayId,
      String title,
      String subtitle,
      String content,
      Long requesterId,
      String status,
      Instant requestedAt,
      Instant processedAt,
      Long reviewerId,
      String rejectionReason,
      LocalDate startDate,
      LocalDate endDate,
      String location,
      List<String> imageUrls) {}
}
