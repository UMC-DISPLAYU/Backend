package com.example.demo.domain.report.application.command;

import com.example.demo.domain.report.domain.type.ReportReason;

public record ReportCommentCommand(
    String commentType,
    Long commentId,
    Long reporterUserId,
    ReportReason reasonCode,
    String description) {}
