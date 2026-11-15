package com.example.demo.domain.admin.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record UserStatusResponse(
    @Schema(description = "회원 ID", example = "12") Long userId,
    @Schema(description = "처리 후 계정 상태", example = "SUSPENDED") String accountStatus,
    @Schema(description = "처리 시각", example = "2026-11-15T14:30:00Z") Instant processedAt) {}
