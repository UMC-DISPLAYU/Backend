package com.example.demo.domain.admin.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResolveReportCaseRequest(
    @Schema(
            description = "NO_ACTION 또는 DELETE_COMMENT",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String action,
    @Schema(description = "처리 사유. 앞뒤 공백 제거 후 1~1,000자", requiredMode = Schema.RequiredMode.REQUIRED)
        String reason) {}
