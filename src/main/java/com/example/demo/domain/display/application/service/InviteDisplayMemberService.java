package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.command.InviteDisplayMemberCommand;
import com.example.demo.domain.display.application.mapper.DisplayMemberInvitationMapper;
import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.application.result.DisplayMemberInvitationResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayInvitationRepository;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.repository.TeamMemberRepository;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InviteDisplayMemberService {

  private final DisplayRepository displayRepository;
  private final DisplayInvitationRepository invitationRepository;
  private final TeamMemberRepository teamMemberRepository;
  private final UserRepository userRepository;
  private final DisplayMemberInvitationMapper mapper;
  private final DisplayPermissionChecker displayPermissionChecker;

  public InviteDisplayMemberService(
      DisplayRepository displayRepository,
      DisplayInvitationRepository invitationRepository,
      TeamMemberRepository teamMemberRepository,
      UserRepository userRepository,
      DisplayMemberInvitationMapper mapper,
      DisplayPermissionChecker displayPermissionChecker) {
    this.displayRepository = displayRepository;
    this.invitationRepository = invitationRepository;
    this.teamMemberRepository = teamMemberRepository;
    this.userRepository = userRepository;
    this.mapper = mapper;
    this.displayPermissionChecker = displayPermissionChecker;
  }

  @Transactional
  public DisplayMemberInvitationResult invite(InviteDisplayMemberCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display =
        displayRepository
            .findById(command.displayId())
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));

    displayPermissionChecker.requireInvitationManager(display, command.requesterUserId());
    validateRole(command.role());
    validateInvitee(command.requesterUserId(), command.inviteeUserId());
    validateNotMember(display.getId(), command.inviteeUserId());
    validatePendingInvitation(display.getId(), command.inviteeUserId());

    DisplayInvitation invitation =
        new DisplayInvitation(
            null,
            new UserId(command.requesterUserId()),
            new UserId(command.inviteeUserId()),
            null,
            null);
    display.addInvitation(invitation);

    try {
      return mapper.toResult(invitationRepository.save(invitation));
    } catch (DataIntegrityViolationException e) {
      throw new BusinessException(DisplayErrorCode.PENDING_DISPLAY_INVITATION_EXISTS, e);
    }
  }

  private void validateRole(TeamMemberRole role) {
    if (role != TeamMemberRole.TEAM_MEM) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_INVITATION_PERMISSION_DENIED);
    }
  }

  private void validateInvitee(Long requesterUserId, Long inviteeUserId) {
    if (requesterUserId.equals(inviteeUserId)) {
      throw new BusinessException(DisplayErrorCode.SELF_INVITATION_NOT_ALLOWED);
    }
    var invitee =
        userRepository
            .findById(inviteeUserId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    if (invitee.getDeletedAt() != null) {
      throw new UserException(UserErrorCode.ALREADY_WITHDRAWN_USER);
    }
  }

  private void validateNotMember(Long displayId, Long inviteeUserId) {
    if (teamMemberRepository.existsAcceptedByDisplayIdAndUserId(displayId, inviteeUserId)) {
      throw new BusinessException(DisplayErrorCode.ALREADY_DISPLAY_MEMBER);
    }
  }

  private void validatePendingInvitation(Long displayId, Long inviteeUserId) {
    if (invitationRepository.existsPendingByDisplayIdAndInviteeUserId(displayId, inviteeUserId)) {
      throw new BusinessException(DisplayErrorCode.PENDING_DISPLAY_INVITATION_EXISTS);
    }
  }
}
