package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.result.DisplayMemberListResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplayMembersService {

  private final DisplayRepository displayRepository;
  private final UserRepository userRepository;

  public GetDisplayMembersService(
      DisplayRepository displayRepository, UserRepository userRepository) {
    this.displayRepository = displayRepository;
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public DisplayMemberListResult getMembers(Long displayId) {
    Display display =
        displayRepository
            .findById(displayId)
            .filter(candidate -> !candidate.isDeleted())
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));

    List<TeamMember> acceptedMembers =
        display.getTeamMembers().stream()
            .filter(teamMember -> !teamMember.isDeleted())
            .filter(TeamMember::isAccepted)
            .toList();
    List<DisplayInvitation> pendingInvitations =
        display.getInvitations().stream().filter(DisplayInvitation::isPending).toList();
    Map<Long, User> usersById =
        userRepository
            .findAllById(
                Stream.concat(
                        acceptedMembers.stream().map(member -> member.getUserId().value()),
                        pendingInvitations.stream()
                            .map(invitation -> invitation.getInviteeUserId().value()))
                    .toList())
            .stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));

    return new DisplayMemberListResult(
        display.getId(),
        acceptedMembers.stream()
            .map(teamMember -> toResult(teamMember, usersById.get(teamMember.getUserId().value())))
            .toList(),
        pendingInvitations.stream()
            .map(
                invitation ->
                    toPendingResult(
                        invitation, usersById.get(invitation.getInviteeUserId().value())))
            .toList());
  }

  private DisplayMemberListResult.TeamMemberResult toResult(TeamMember teamMember, User user) {
    return new DisplayMemberListResult.TeamMemberResult(
        teamMember.getId(),
        teamMember.getUserId().value(),
        teamMember.getDisplayNickname(),
        user != null && user.getDeletedAt() == null,
        user != null && user.isVerified(),
        teamMember.isAccepted(),
        teamMember.getRole().name());
  }

  private DisplayMemberListResult.TeamMemberResult toPendingResult(
      DisplayInvitation invitation, User user) {
    Long inviteeUserId = invitation.getInviteeUserId().value();
    return new DisplayMemberListResult.TeamMemberResult(
        null,
        inviteeUserId,
        user != null ? user.getNickname() : "",
        user != null && user.getDeletedAt() == null,
        user != null && user.isVerified(),
        false,
        TeamMemberRole.TEAM_MEM.name());
  }
}
