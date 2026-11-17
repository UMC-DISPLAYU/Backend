package com.example.demo.domain.report.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record CreateContentReportResponse(
    @Schema(description = "생성된 신고 ID") Long reportId,
    @Schema(description = "신고 생성 시각 (UTC)") LocalDateTime createdAt) {}
