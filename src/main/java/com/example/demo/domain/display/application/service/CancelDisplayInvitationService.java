package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.command.CancelDisplayInvitationCommand;
import com.example.demo.domain.display.application.mapper.DisplayMemberInvitationMapper;
import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.application.result.DisplayMemberInvitationResult;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayInvitationRepository;
import com.example.demo.global.error.BusinessException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelDisplayInvitationService {
  private final DisplayInvitationRepository invitationRepository;
  private final DisplayPermissionChecker permissionChecker;
  private final DisplayMemberInvitationMapper mapper;
  private final Clock clock;

  @Transactional
  public DisplayMemberInvitationResult cancel(CancelDisplayInvitationCommand command) {
    DisplayInvitation invitation =
        invitationRepository
            .findByIdForUpdate(command.invitationId())
            .orElseThrow(
                () -> new BusinessException(DisplayErrorCode.DISPLAY_MEMBER_INVITATION_NOT_FOUND));
    if (invitation.getDisplay().isDeleted()) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_MEMBER_INVITATION_NOT_FOUND);
    }
    permissionChecker.requireInvitationManager(invitation.getDisplay(), command.requesterUserId());
    invitation.cancel(LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
    return mapper.toResult(invitation);
  }
}
