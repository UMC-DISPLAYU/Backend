package com.example.demo.domain.admin.presentation.response;

import java.time.Instant;
import java.util.List;

public final class OperationLogResponses {
  private OperationLogResponses() {}

  public record Page(List<Item> items, Long nextCursor, boolean hasNext) {}

  public record Item(
      Long operationId,
      String targetType,
      Long targetId,
      String action,
      Long operatorId,
      String reason,
      Instant processedAt) {}
}
