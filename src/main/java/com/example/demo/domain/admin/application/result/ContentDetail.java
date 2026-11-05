package com.example.demo.domain.admin.application.result;

import java.time.Instant;
import java.util.List;

public record ContentDetail(
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
    List<History> history) {
  public record History(
      Long operationId, String action, Long operatorId, String reason, Instant processedAt) {}
}
