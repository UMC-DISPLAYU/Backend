package com.example.demo.domain.display.presentation.response;

import java.time.LocalDateTime;

public record DisplayMemberInvitationResponse(
    Long invitationId,
    Long displayId,
    Long inviterUserId,
    Long inviteeUserId,
    String status,
    LocalDateTime createdAt,
    LocalDateTime respondedAt) {}
