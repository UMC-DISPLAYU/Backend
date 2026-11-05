package com.example.demo.domain.admin.application.result;

import java.time.Instant;
import java.util.List;

public record ContentStatusResult(
    String contentType,
    Long contentId,
    String operationStatus,
    String parentOperationStatus,
    boolean viewerVisible,
    List<String> visibilityReasons,
    Instant processedAt) {}
