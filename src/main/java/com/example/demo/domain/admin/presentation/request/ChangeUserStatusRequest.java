package com.example.demo.domain.admin.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeUserStatusRequest(
    @Schema(
            description = "목표 상태",
            example = "SUSPENDED",
            allowableValues = {"ACTIVE", "SUSPENDED"},
            requiredMode = Schema.RequiredMode.REQUIRED)
        String accountStatus,
    @Schema(
            description = "정지·해제 사유. 앞뒤 공백 제거 후 1~1,000자",
            example = "운영 정책 위반",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String reason) {}
