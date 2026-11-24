package com.example.demo.domain.report.presentation.request;

import com.example.demo.domain.report.domain.type.ReportReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommentReportRequest(
    @NotNull @Schema(description = "신고 사유 코드", requiredMode = Schema.RequiredMode.REQUIRED)
        ReportReason reasonCode,
    @Size(max = 1000) @Schema(description = "신고 상세 설명 (최대 1,000자)") String description) {}
