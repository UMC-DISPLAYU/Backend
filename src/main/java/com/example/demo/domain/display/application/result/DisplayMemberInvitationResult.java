package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import java.time.LocalDateTime;

public record DisplayMemberInvitationResult(
    Long invitationId,
    Long displayId,
    Long inviterUserId,
    Long inviteeUserId,
    String status,
    LocalDateTime createdAt,
    LocalDateTime respondedAt) {

  public static DisplayMemberInvitationResult from(DisplayInvitation invitation) {
    return new DisplayMemberInvitationResult(
        invitation.getId(),
        invitation.getDisplay().getId(),
        invitation.getInviterUserId().value(),
        invitation.getInviteeUserId().value(),
        invitation.getStatus().name(),
        invitation.getCreatedAt(),
        invitation.getRespondedAt());
  }
}
