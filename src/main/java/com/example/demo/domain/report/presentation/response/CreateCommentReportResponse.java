package com.example.demo.domain.report.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record CreateCommentReportResponse(
    @Schema(description = "생성된 신고 ID") Long reportId,
    @Schema(description = "신고 접수 시각 (UTC)") Instant reportedAt) {}
