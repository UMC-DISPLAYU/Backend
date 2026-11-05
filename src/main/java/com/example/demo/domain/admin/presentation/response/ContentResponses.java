package com.example.demo.domain.admin.presentation.response;

import java.time.Instant;
import java.util.List;

public final class ContentResponses {
  private ContentResponses() {}

  public record Summary(
      String contentType,
      Long contentId,
      String title,
      Long ownerUserId,
      String ownerName,
      String operationStatus,
      String parentOperationStatus,
      boolean viewerVisible,
      List<String> visibilityReasons,
      Instant createdAt,
      String thumbnailUrl) {}

  public record Page(List<Summary> items, String nextCursor, boolean hasNext) {}

  public record Detail(
      String contentType,
      Long contentId,
      String title,
      Long ownerUserId,
      String ownerName,
      String operationStatus,
      String parentOperationStatus,
      boolean viewerVisible,
      List<String> visibilityReasons,
      Instant createdAt,
      String thumbnailUrl,
      String content,
      List<String> imageUrls,
      List<History> history) {}

  public record History(
      Long operationId, String action, Long operatorId, String reason, Instant processedAt) {}
}
