package com.example.demo.domain.display.application.result;

import java.time.LocalDateTime;

public record DisplayMemberInvitationResult(
    Long invitationId,
    Long displayId,
    Long inviterUserId,
    Long inviteeUserId,
    String status,
    LocalDateTime createdAt,
    LocalDateTime respondedAt) {}
