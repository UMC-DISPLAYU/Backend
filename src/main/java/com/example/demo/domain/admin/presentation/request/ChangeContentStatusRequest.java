package com.example.demo.domain.admin.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeContentStatusRequest(
    @Schema(
            description = "목표 운영 상태",
            allowableValues = {"ACTIVE", "SUSPENDED"},
            requiredMode = Schema.RequiredMode.REQUIRED)
        String operationStatus,
    @Schema(
            description = "중단·재개 사유. 앞뒤 공백 제거 후 1~1,000자",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String reason) {}
