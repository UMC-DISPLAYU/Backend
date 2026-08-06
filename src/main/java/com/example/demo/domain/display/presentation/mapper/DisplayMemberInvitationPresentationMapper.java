package com.example.demo.domain.display.presentation.mapper;

import com.example.demo.domain.display.application.command.AcceptDisplayInvitationCommand;
import com.example.demo.domain.display.application.command.InviteDisplayMemberCommand;
import com.example.demo.domain.display.application.command.RejectDisplayInvitationCommand;
import com.example.demo.domain.display.application.result.DisplayMemberInvitationResult;
import com.example.demo.domain.display.application.result.DisplayMemberListResult;
import com.example.demo.domain.display.application.result.DisplayMemberResult;
import com.example.demo.domain.display.application.result.GraduationDisplayResult;
import com.example.demo.domain.display.application.result.MyDisplayInvitationListResult;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.presentation.request.AcceptDisplayInvitationRequest;
import com.example.demo.domain.display.presentation.request.InviteDisplayMemberRequest;
import com.example.demo.domain.display.presentation.request.InviteDisplayMemberRoleRequest;
import com.example.demo.domain.display.presentation.response.DisplayMemberInvitationResponse;
import com.example.demo.domain.display.presentation.response.DisplayMemberListResponse;
import com.example.demo.domain.display.presentation.response.GraduationDisplayResponse;
import com.example.demo.domain.display.presentation.response.MyDisplayInvitationListResponse;
import org.springframework.stereotype.Component;

@Component
public class DisplayMemberInvitationPresentationMapper {

  public InviteDisplayMemberCommand toCommand(
      Long requesterUserId, Long displayId, InviteDisplayMemberRequest request) {
    return new InviteDisplayMemberCommand(
        requesterUserId, displayId, request.inviteeUserId(), toTeamMemberRole(request.role()));
  }

  private TeamMemberRole toTeamMemberRole(InviteDisplayMemberRoleRequest role) {
    if (role == null) {
      return TeamMemberRole.TEAM_MEM;
    }
    return switch (role) {
      case TEAM_MEM -> TeamMemberRole.TEAM_MEM;
    };
  }

  public AcceptDisplayInvitationCommand toAcceptCommand(
      Long requesterUserId, Long invitationId, AcceptDisplayInvitationRequest request) {
    return new AcceptDisplayInvitationCommand(
        requesterUserId, invitationId, request.displayNickname());
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

  public DisplayMemberListResponse toResponse(DisplayMemberListResult result) {
    return new DisplayMemberListResponse(
        result.displayId(), result.members().stream().map(this::toResponse).toList());
  }

  public DisplayMemberListResponse.TeamMemberResponse toResponse(DisplayMemberResult result) {
    return new DisplayMemberListResponse.TeamMemberResponse(
        result.teamMemberId(),
        result.userId(),
        result.displayNickname(),
        result.loggedIn(),
        result.artistVerified(),
        result.accepted(),
        result.role());
  }

  public MyDisplayInvitationListResponse toResponse(MyDisplayInvitationListResult result) {
    return new MyDisplayInvitationListResponse(
        result.invitations().stream().map(this::toResponse).toList());
  }

  public GraduationDisplayResponse toResponse(GraduationDisplayResult result) {
    return new GraduationDisplayResponse(
        result.exhibitions().stream().map(this::toResponse).toList());
  }

  private DisplayMemberListResponse.TeamMemberResponse toResponse(
      DisplayMemberListResult.TeamMemberResult result) {
    return new DisplayMemberListResponse.TeamMemberResponse(
        result.teamMemberId(),
        result.userId(),
        result.displayNickname(),
        result.loggedIn(),
        result.artistVerified(),
        result.accepted(),
        result.role());
  }

  private MyDisplayInvitationListResponse.InvitationResponse toResponse(
      MyDisplayInvitationListResult.InvitationResult result) {
    return new MyDisplayInvitationListResponse.InvitationResponse(
        result.invitationId(),
        result.displayId(),
        result.thumbnailUrl(),
        result.startDate(),
        result.endDate(),
        result.location(),
        result.leaderName(),
        result.title(),
        result.placeName());
  }

  private GraduationDisplayResponse.ExhibitionResponse toResponse(
      GraduationDisplayResult.ExhibitionResult result) {
    return new GraduationDisplayResponse.ExhibitionResponse(
        result.displayId(),
        result.title(),
        result.posterImageUrl(),
        result.organization(),
        result.department(),
        result.startedAt(),
        result.endedAt(),
        result.dayLeft(),
        result.isBookmarked());
  }
}
