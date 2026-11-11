package com.example.demo.domain.admin.presentation.response;

import java.time.Instant;

public record ReportResolutionResponse(
    Long reportCaseId,
    String status,
    String action,
    String commentType,
    Long commentId,
    Long operatorId,
    Instant processedAt) {}
