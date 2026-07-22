package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.application.result.DisplayMemberInvitationResult;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayInvitationRepository;
import com.example.demo.global.error.BusinessException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RejectDisplayInvitationService {

  private final DisplayInvitationRepository invitationRepository;
  private final Clock clock;

  public RejectDisplayInvitationService(
      DisplayInvitationRepository invitationRepository, Clock clock) {
    this.invitationRepository = invitationRepository;
    this.clock = clock;
  }

  @Transactional
  public DisplayMemberInvitationResult reject(RejectDisplayInvitationCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    DisplayInvitation invitation =
        invitationRepository
            .findByIdForUpdate(command.invitationId())
            .orElseThrow(
                () -> new BusinessException(DisplayErrorCode.DISPLAY_MEMBER_INVITATION_NOT_FOUND));
    if (!invitation.isInvitee(command.requesterUserId())) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_INVITATION_INVITEE_MISMATCH);
    }

    invitation.reject(LocalDateTime.now(clock));
    return DisplayMemberInvitationResult.from(invitation);
  }
}
