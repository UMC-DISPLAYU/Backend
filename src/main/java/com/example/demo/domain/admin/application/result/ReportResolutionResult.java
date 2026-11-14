package com.example.demo.domain.admin.application.result;

import java.time.Instant;

public record ReportResolutionResult(
    Long reportCaseId,
    String status,
    String action,
    String commentType,
    Long commentId,
    Long operatorId,
    Instant processedAt) {}
