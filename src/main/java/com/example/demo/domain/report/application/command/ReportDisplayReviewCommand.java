package com.example.demo.domain.report.application.command;

import com.example.demo.domain.report.domain.type.ReportReason;

public record ReportDisplayReviewCommand(
    Long displayId, Long reviewId, Long reporterUserId, ReportReason reason) {}
