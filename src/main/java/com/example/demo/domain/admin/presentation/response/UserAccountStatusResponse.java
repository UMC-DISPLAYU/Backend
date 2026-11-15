package com.example.demo.domain.admin.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record UserAccountStatusResponse(
    @Schema(description = "회원 ID", example = "12") Long userId,
    @Schema(
            description = "계정 상태",
            example = "SUSPENDED",
            allowableValues = {"ACTIVE", "SUSPENDED"})
        String accountStatus,
    @Schema(
            description = "정지 처리 시각. ACTIVE 계정은 null",
            example = "2026-11-15T14:30:00Z",
            nullable = true)
        Instant suspendedAt,
    @Schema(description = "정지 처리 관리자 ID. ACTIVE 계정은 null", example = "3", nullable = true)
        Long operatorId,
    @Schema(description = "정지 사유. ACTIVE 계정은 null", example = "운영 정책 위반", nullable = true)
        String reason) {}
