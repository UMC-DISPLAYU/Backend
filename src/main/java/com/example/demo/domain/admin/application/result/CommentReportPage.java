package com.example.demo.domain.admin.application.result;

import java.time.Instant;
import java.util.List;

public record CommentReportPage(List<Item> items, Long nextCursor, boolean hasNext) {
  public record Item(
      Long reportId,
      Long reporterUserId,
      String reasonCode,
      String description,
      Instant reportedAt) {}
}
