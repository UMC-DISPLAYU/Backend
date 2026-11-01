package com.example.demo.domain.display.presentation.mapper;

import com.example.demo.domain.display.application.command.ChangeDisplayMemberRoleCommand;
import com.example.demo.domain.display.application.command.TransferDisplayLeaderCommand;
import com.example.demo.domain.display.application.result.DisplayLeaderTransferResult;
import com.example.demo.domain.display.application.result.DisplayMemberRoleResult;
import com.example.demo.domain.display.application.result.RemoveDisplayMemberResult;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.presentation.request.ChangeDisplayMemberRoleRequest;
import com.example.demo.domain.display.presentation.request.DisplayMemberRoleRequest;
import com.example.demo.domain.display.presentation.request.TransferDisplayLeaderRequest;
import com.example.demo.domain.display.presentation.response.DisplayLeaderTransferResponse;
import com.example.demo.domain.display.presentation.response.DisplayMemberRoleResponse;
import com.example.demo.domain.display.presentation.response.RemoveDisplayMemberResponse;
import org.springframework.stereotype.Component;

@Component
public class DisplayMemberManagementPresentationMapper {

  public ChangeDisplayMemberRoleCommand toCommand(
      Long requesterUserId,
      Long displayId,
      Long teamMemberId,
      ChangeDisplayMemberRoleRequest request) {
    return new ChangeDisplayMemberRoleCommand(
        requesterUserId, displayId, teamMemberId, toTeamMemberRole(request.role()));
  }

  public TransferDisplayLeaderCommand toCommand(
      Long requesterUserId, Long displayId, TransferDisplayLeaderRequest request) {
    return new TransferDisplayLeaderCommand(requesterUserId, displayId, request.targetMemberId());
  }

  public DisplayMemberRoleResponse toResponse(DisplayMemberRoleResult result) {
    return new DisplayMemberRoleResponse(
        result.displayId(), result.teamMemberId(), result.userId(), result.role());
  }

  public DisplayLeaderTransferResponse toResponse(DisplayLeaderTransferResult result) {
    return new DisplayLeaderTransferResponse(
        result.displayId(),
        toMemberResponse(result.previousLeader()),
        toMemberResponse(result.newLeader()));
  }

  public RemoveDisplayMemberResponse toResponse(RemoveDisplayMemberResult result) {
    return new RemoveDisplayMemberResponse(
        result.displayId(),
        result.removedMemberId(),
        result.removedUserId(),
        result.deletedArtworkCount());
  }

  private DisplayLeaderTransferResponse.MemberResponse toMemberResponse(
      DisplayMemberRoleResult result) {
    return new DisplayLeaderTransferResponse.MemberResponse(
        result.teamMemberId(), result.userId(), result.role());
  }

  private TeamMemberRole toTeamMemberRole(DisplayMemberRoleRequest role) {
    return switch (role) {
      case TEAM_MEM -> TeamMemberRole.TEAM_MEM;
      case MANAGER -> TeamMemberRole.MANAGER;
      case TEAM_LEADER -> TeamMemberRole.TEAM_LEADER;
    };
  }
}
