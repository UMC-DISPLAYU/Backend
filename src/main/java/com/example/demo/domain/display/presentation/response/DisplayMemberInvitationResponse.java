package com.example.demo.domain.display.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record DisplayMemberInvitationResponse(
    @Schema(example = "1") Long invitationId,
    @Schema(example = "10") Long displayId,
    @Schema(example = "101") Long inviterUserId,
    @Schema(example = "102") Long inviteeUserId,
    @Schema(example = "PENDING") String status,
    @Schema(example = "2026-11-14T04:00:00") LocalDateTime createdAt,
    @Schema(example = "2026-11-14T04:10:00", nullable = true) LocalDateTime respondedAt) {}
