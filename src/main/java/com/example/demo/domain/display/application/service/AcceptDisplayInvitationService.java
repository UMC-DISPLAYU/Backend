package com.example.demo.domain.display.application.service;

import com.example.demo.domain.artist.application.permission.ArtistPermissionChecker;
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
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.global.error.BusinessException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
  private final UserRepository userRepository;
  private final ArtistPermissionChecker artistPermissionChecker;

  public AcceptDisplayInvitationService(
      DisplayInvitationRepository invitationRepository,
      TeamMemberRepository teamMemberRepository,
      DisplayRepository displayRepository,
      DisplayMemberInvitationMapper mapper,
      Clock clock,
      UserRepository userRepository,
      ArtistPermissionChecker artistPermissionChecker) {
    this.invitationRepository = invitationRepository;
    this.teamMemberRepository = teamMemberRepository;
    this.displayRepository = displayRepository;
    this.mapper = mapper;
    this.clock = clock;
    this.userRepository = userRepository;
    this.artistPermissionChecker = artistPermissionChecker;
  }

  @Transactional
  public DisplayMemberInvitationResult accept(AcceptDisplayInvitationCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    DisplayInvitation invitation = findInvitationForUpdate(command.invitationId());
    if (invitation.getDisplay().isDeleted()) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_MEMBER_INVITATION_NOT_FOUND);
    }
    validateInvitee(invitation, command.requesterUserId());
    User invitee = findVerifiedInvitee(command.requesterUserId());

    Long displayId = invitation.getDisplay().getId();
    Long inviteeUserId = invitation.getInviteeUserId().value();
    if (teamMemberRepository.existsAcceptedByDisplayIdAndUserId(displayId, inviteeUserId)) {
      throw new BusinessException(DisplayErrorCode.ALREADY_DISPLAY_MEMBER);
    }

    invitation.accept(LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
    Display display = invitation.getDisplay();
    // 수락 요청에서 검증된 전시 닉네임을 새 팀원의 표시 이름으로 저장한다.
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

  private User findVerifiedInvitee(Long inviteeUserId) {
    User invitee =
        userRepository
            .findById(inviteeUserId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    // 초대 이후 인증 상태가 달라질 수 있으므로 수락 시점에도 작가 인증을 다시 확인한다.
    artistPermissionChecker.requireVerified(invitee);
    return invitee;
  }
}
