package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.command.AcceptDisplayInvitationCommand;
import com.example.demo.domain.display.application.mapper.DisplayMemberInvitationMapper;
import com.example.demo.domain.display.application.result.DisplayMemberInvitationResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayInvitationRepository;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.repository.TeamMemberRepository;
import com.example.demo.global.error.BusinessException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcceptDisplayInvitationService {

  private final DisplayInvitationRepository invitationRepository;
  private final TeamMemberRepository teamMemberRepository;
  private final DisplayRepository displayRepository;
  private final DisplayMemberInvitationMapper mapper;
  private final Clock clock;

  public AcceptDisplayInvitationService(
      DisplayInvitationRepository invitationRepository,
      TeamMemberRepository teamMemberRepository,
      DisplayRepository displayRepository,
      DisplayMemberInvitationMapper mapper,
      Clock clock) {
    this.invitationRepository = invitationRepository;
    this.teamMemberRepository = teamMemberRepository;
    this.displayRepository = displayRepository;
    this.mapper = mapper;
    this.clock = clock;
  }

  @Transactional
  public DisplayMemberInvitationResult accept(AcceptDisplayInvitationCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    DisplayInvitation invitation = findInvitationForUpdate(command.invitationId());
    validateInvitee(invitation, command.requesterUserId());

    Long displayId = invitation.getDisplay().getId();
    Long inviteeUserId = invitation.getInviteeUserId().value();
    if (teamMemberRepository.existsAcceptedByDisplayIdAndUserId(displayId, inviteeUserId)) {
      throw new BusinessException(DisplayErrorCode.ALREADY_DISPLAY_MEMBER);
    }

    invitation.accept(LocalDateTime.now(clock));
    Display display = invitation.getDisplay();
    TeamMember teamMember = display.inviteeAsTeamMember(invitation, command.displayNickname());

    try {
      teamMemberRepository.save(teamMember);
      displayRepository.save(display);
      return mapper.toResult(invitation);
    } catch (DataIntegrityViolationException e) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_MEMBER_CONCURRENTLY_CREATED, e);
    }
  }

  private DisplayInvitation findInvitationForUpdate(Long invitationId) {
    return invitationRepository
        .findByIdForUpdate(invitationId)
        .orElseThrow(
            () -> new BusinessException(DisplayErrorCode.DISPLAY_MEMBER_INVITATION_NOT_FOUND));
  }

  private void validateInvitee(DisplayInvitation invitation, Long requesterUserId) {
    if (!invitation.isInvitee(requesterUserId)) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_INVITATION_INVITEE_MISMATCH);
    }
  }
}
