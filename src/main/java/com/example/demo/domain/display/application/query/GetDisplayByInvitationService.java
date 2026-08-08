package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayInvitationRepository;
import com.example.demo.domain.display.domain.repository.DisplayLikeRepository;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.DisplayInvitationTokenHasher;
import com.example.demo.global.error.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplayByInvitationService {

  private final DisplayRepository displayRepository;
  private final DisplayLikeRepository displayLikeRepository;
  private final DisplayInvitationRepository invitationRepository;
  private final DisplayInvitationTokenHasher tokenHasher;

  public GetDisplayByInvitationService(
      DisplayRepository displayRepository,
      DisplayLikeRepository displayLikeRepository,
      DisplayInvitationRepository invitationRepository,
      DisplayInvitationTokenHasher tokenHasher) {
    this.displayRepository = displayRepository;
    this.displayLikeRepository = displayLikeRepository;
    this.invitationRepository = invitationRepository;
    this.tokenHasher = tokenHasher;
  }

  @Transactional
  public DisplayDetailResult getDisplay(String rawToken, Long requesterUserId) {
    String tokenHash = tokenHasher.hash(rawToken);
    return displayRepository
        .findByInvitationToken(tokenHash)
        .map(
            display -> {
              display.validateInvitationAccessible();
              createPendingInvitationIfNeeded(display, requesterUserId);
              return DisplayDetailResult.from(
                  display,
                  displayLikeRepository.countByDisplayIdAndDeletedAtIsNull(display.getId()));
            })
        .orElseThrow(
            () -> new BusinessException(DisplayErrorCode.INVALID_DISPLAY_INVITATION_TOKEN));
  }

  private void createPendingInvitationIfNeeded(Display display, Long requesterUserId) {
    if (display.hasAcceptedTeamMember(requesterUserId)
        || invitationRepository.existsPendingByDisplayIdAndInviteeUserId(
            display.getId(), requesterUserId)) {
      return;
    }

    DisplayInvitation invitation =
        new DisplayInvitation(
            null, display.getOwnerUserId(), new UserId(requesterUserId), null, null);
    display.addInvitation(invitation);

    try {
      invitationRepository.save(invitation);
    } catch (DataIntegrityViolationException ignored) {
      // Concurrent token reads can race on the pending invitation unique constraint.
    }
  }
}
