package com.example.demo.domain.admin.application.result;

import java.time.Instant;

public record ReportCaseSummary(
    Long reportCaseId,
    String status,
    String commentType,
    Long commentId,
    long reportCount,
    Instant lastReportedAt) {}
