package com.example.demo.domain.displayartwork.application.permission;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.displayartwork.domain.entity.Creator;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.ArtistVerificationRepository;
import com.example.demo.domain.displayartwork.domain.repository.CreatorRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class DisplayArtworkPermissionChecker {

  private final CreatorRepository creatorRepository;
  private final ArtistVerificationRepository artistVerificationRepository;

  public DisplayArtworkPermissionChecker(
      CreatorRepository creatorRepository,
      ArtistVerificationRepository artistVerificationRepository) {
    this.creatorRepository = creatorRepository;
    this.artistVerificationRepository = artistVerificationRepository;
  }

  public void requireArtworkRegistrant(Long userId, Display display) {
    requireArtworkParticipant(userId, display);
    if (!artistVerificationRepository.isVerifiedArtist(userId)) {
      throw new BusinessException(DisplayArtworkErrorCode.NOT_VERIFIED_ARTIST);
    }
  }

  public void requireArtworkParticipant(Long userId, Display display) {
    if (!isDisplayParticipant(display, userId)) {
      throw new BusinessException(DisplayArtworkErrorCode.NOT_DISPLAY_TEAM_MEMBER);
    }
  }

  public void requireArtworkEditor(Long userId, Display display, Long artworkId) {
    requireArtworkEditor(
        userId, display, artworkId, DisplayArtworkErrorCode.FORBIDDEN_ARTWORK_EDIT);
  }

  public void requireArtworkDeleter(Long userId, Display display, Long artworkId) {
    requireArtworkEditor(
        userId, display, artworkId, DisplayArtworkErrorCode.FORBIDDEN_ARTWORK_ACTION);
  }

  public void requireArtworkOrderEditor(Long userId, Display display) {
    if (!display.isOwner(userId)) {
      throw new BusinessException(DisplayArtworkErrorCode.FORBIDDEN_ARTWORK_ORDER_EDIT);
    }
  }

  public void requireProxyRegistrationAllowed(
      Long requesterUserId, Display display, Long artistUserId) {
    if (!Objects.equals(requesterUserId, artistUserId)
        && !isDisplayLeader(display, requesterUserId)) {
      throw new BusinessException(DisplayArtworkErrorCode.FORBIDDEN_PROXY_ARTWORK_REGISTRATION);
    }
  }

  public void requireQnaHandlerAssignable(
      Display display, Long handlerUserId, Set<Long> artistUserIds) {
    if (!artistUserIds.contains(handlerUserId) && !isDisplayLeader(display, handlerUserId)) {
      throw new BusinessException(DisplayArtworkErrorCode.INVALID_QA_HANDLER);
    }
  }

  public void requireVerifiedArtistParticipant(
      Display display, Long userId, DisplayArtworkErrorCode errorCode) {
    if (!isDisplayParticipant(display, userId)
        || !artistVerificationRepository.isVerifiedArtist(userId)) {
      throw new BusinessException(errorCode);
    }
  }

  public TeamMember requireVerifiedArtistTeamMember(
      Display display, Long userId, DisplayArtworkErrorCode errorCode) {
    TeamMember teamMember =
        display.getTeamMembers().stream()
            .filter(member -> !member.isDeleted())
            .filter(TeamMember::isAccepted)
            .filter(member -> Objects.equals(member.getUserId().value(), userId))
            .findFirst()
            .orElseThrow(() -> new BusinessException(errorCode));
    if (!artistVerificationRepository.isVerifiedArtist(userId)) {
      throw new BusinessException(errorCode);
    }
    return teamMember;
  }

  private void requireArtworkEditor(
      Long userId,
      Display display,
      Long artworkId,
      DisplayArtworkErrorCode permissionDeniedErrorCode) {
    if (userId == null) {
      throw new BusinessException(permissionDeniedErrorCode);
    }
    if (isDisplayLeader(display, userId)) {
      return;
    }
    boolean isCreator =
        creatorRepository.findByDisplayArtworkId(artworkId).stream()
            .map(Creator::getUserId)
            .anyMatch(creatorUserId -> Objects.equals(creatorUserId, userId));
    if (!isCreator) {
      throw new BusinessException(permissionDeniedErrorCode);
    }
  }

  private boolean isDisplayParticipant(Display display, Long userId) {
    return display.isOwner(userId) || display.hasAcceptedTeamMember(userId);
  }

  private boolean isDisplayLeader(Display display, Long userId) {
    return display.isOwner(userId) || display.isTeamLeader(userId);
  }
}
