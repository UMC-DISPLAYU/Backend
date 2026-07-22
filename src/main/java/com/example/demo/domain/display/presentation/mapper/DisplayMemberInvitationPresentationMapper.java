package com.example.demo.domain.display.presentation.mapper;

import com.example.demo.domain.display.application.command.AcceptDisplayInvitationCommand;
import com.example.demo.domain.display.application.command.InviteDisplayMemberCommand;
import com.example.demo.domain.display.application.command.RejectDisplayInvitationCommand;
import com.example.demo.domain.display.application.result.DisplayMemberInvitationResult;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.presentation.request.InviteDisplayMemberRequest;
import com.example.demo.domain.display.presentation.response.DisplayMemberInvitationResponse;
import org.springframework.stereotype.Component;

@Component
public class DisplayMemberInvitationPresentationMapper {

  public InviteDisplayMemberCommand toCommand(
      Long requesterUserId, Long displayId, InviteDisplayMemberRequest request) {
    return new InviteDisplayMemberCommand(
        requesterUserId,
        displayId,
        request.inviteeUserId(),
        request.role() == null ? TeamMemberRole.TEAM_MEM : request.role());
  }

  public AcceptDisplayInvitationCommand toAcceptCommand(Long requesterUserId, Long invitationId) {
    return new AcceptDisplayInvitationCommand(requesterUserId, invitationId);
  }

  public RejectDisplayInvitationCommand toRejectCommand(Long requesterUserId, Long invitationId) {
    return new RejectDisplayInvitationCommand(requesterUserId, invitationId);
  }

  public DisplayMemberInvitationResponse toResponse(DisplayMemberInvitationResult result) {
    return new DisplayMemberInvitationResponse(
        result.invitationId(),
        result.displayId(),
        result.inviterUserId(),
        result.inviteeUserId(),
        result.status(),
        result.createdAt(),
        result.respondedAt());
  }
}
