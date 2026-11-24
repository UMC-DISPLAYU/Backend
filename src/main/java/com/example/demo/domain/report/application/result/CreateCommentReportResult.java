package com.example.demo.domain.report.application.result;

import java.time.Instant;

public record CreateCommentReportResult(Long reportId, Instant reportedAt) {}
