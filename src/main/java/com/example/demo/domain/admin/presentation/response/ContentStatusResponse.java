package com.example.demo.domain.admin.presentation.response;

import java.time.Instant;
import java.util.List;

public record ContentStatusResponse(
    String contentType,
    Long contentId,
    String operationStatus,
    String parentOperationStatus,
    boolean viewerVisible,
    List<String> visibilityReasons,
    Instant processedAt) {}
