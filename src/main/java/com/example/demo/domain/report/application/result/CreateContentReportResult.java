package com.example.demo.domain.report.application.result;

import java.time.LocalDateTime;

public record CreateContentReportResult(Long reportId, LocalDateTime createdAt) {}
