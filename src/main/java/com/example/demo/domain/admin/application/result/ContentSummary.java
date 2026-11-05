package com.example.demo.domain.admin.application.result;

import java.time.Instant;
import java.util.List;

public record ContentSummary(
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
