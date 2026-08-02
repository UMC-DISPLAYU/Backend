package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.displayartwork.application.result.AuthorSetupResult;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.Creator;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.ArtistVerificationRepository;
import com.example.demo.domain.displayartwork.domain.repository.CreatorRepository;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.global.error.BusinessException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorSetupService {

  private final DisplayArtworkRepository displayArtworkRepository;
  private final CreatorRepository creatorRepository;
  private final ArtistVerificationRepository artistVerificationRepository;

  public AuthorSetupService(
      DisplayArtworkRepository displayArtworkRepository,
      CreatorRepository creatorRepository,
      ArtistVerificationRepository artistVerificationRepository) {
    this.displayArtworkRepository = displayArtworkRepository;
    this.creatorRepository = creatorRepository;
    this.artistVerificationRepository = artistVerificationRepository;
  }

  @Transactional
  public AuthorSetupResult setup(Long requesterUserId, AuthorSetupCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    DisplayArtwork artwork =
        displayArtworkRepository
            .findById(command.artworkId())
            .filter(a -> !a.isDeleted())
            .orElseThrow(
                () -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND));
    Display display = artwork.getDisplay();

    validateRequester(display, requesterUserId);

    Long artistUserId = command.artistUserId();
    if (artistUserId != null) {
      requireVerifiedParticipant(
          display, artistUserId, DisplayArtworkErrorCode.INVALID_ARTIST_USER_ID);
    }

    List<Long> coAuthorUserIds = command.coAuthorUserIds();
    if (coAuthorUserIds.contains(artistUserId)
        || new HashSet<>(coAuthorUserIds).size() != coAuthorUserIds.size()) {
      throw new BusinessException(DisplayArtworkErrorCode.INVALID_CO_AUTHOR);
    }
    Map<Long, String> coAuthorNames = resolveCoAuthorNames(display, coAuthorUserIds);

    Set<Long> qaHandlerCandidates = new HashSet<>(coAuthorUserIds);
    if (artistUserId != null) {
      qaHandlerCandidates.add(artistUserId);
    }
    if (!qaHandlerCandidates.contains(command.qaHandlerUserId())) {
      throw new BusinessException(DisplayArtworkErrorCode.INVALID_QA_HANDLER);
    }

    creatorRepository.deleteAllByDisplayArtworkId(command.artworkId());

    List<Creator> creators = new ArrayList<>();
    creators.add(
        new Creator(
            null,
            command.artistName(),
            command.qaHandlerUserId().equals(artistUserId),
            true,
            artistUserId,
            command.artworkId()));
    for (Long coAuthorUserId : coAuthorUserIds) {
      creators.add(
          new Creator(
              null,
              coAuthorNames.get(coAuthorUserId),
              command.qaHandlerUserId().equals(coAuthorUserId),
              false,
              coAuthorUserId,
              command.artworkId()));
    }
    for (String rawName : command.coAuthorRawNames()) {
      creators.add(new Creator(null, rawName, false, false, null, command.artworkId()));
    }
    creatorRepository.saveAll(creators);

    int coAuthorCount = coAuthorUserIds.size() + command.coAuthorRawNames().size();
    return new AuthorSetupResult(
        command.artworkId(),
        command.artistName(),
        artistUserId,
        coAuthorCount,
        command.qaHandlerUserId());
  }

  private void validateRequester(Display display, Long requesterUserId) {
    // 전시를 만든 소유자는 TeamMember로 등록되지 않으므로 소유자 여부도 함께 확인한다.
    boolean isAcceptedTeamMember =
        display.isOwner(requesterUserId)
            || display.getTeamMembers().stream()
                .anyMatch(
                    teamMember ->
                        teamMember.isAccepted()
                            && teamMember.getUserId().value().equals(requesterUserId));
    if (!isAcceptedTeamMember) {
      throw new BusinessException(DisplayArtworkErrorCode.NOT_DISPLAY_TEAM_MEMBER);
    }
    if (!artistVerificationRepository.isVerifiedArtist(requesterUserId)) {
      throw new BusinessException(DisplayArtworkErrorCode.NOT_VERIFIED_ARTIST);
    }
  }

  /**
   * 전시에 참여할 수 있는 작가 인증 사용자인지 검증한다. 전시 소유자는 TeamMember로 등록되지 않으므로 소유자도 참여자로 인정한다. 팀원 정보(닉네임)가 필요한
   * 경우에는 {@link #requireVerifiedTeamMember}를 사용한다.
   */
  private void requireVerifiedParticipant(
      Display display, Long userId, DisplayArtworkErrorCode errorCode) {
    if (display.isOwner(userId)) {
      if (!artistVerificationRepository.isVerifiedArtist(userId)) {
        throw new BusinessException(errorCode);
      }
      return;
    }
    requireVerifiedTeamMember(display, userId, errorCode);
  }

  private TeamMember requireVerifiedTeamMember(
      Display display, Long userId, DisplayArtworkErrorCode errorCode) {
    TeamMember member =
        display.getTeamMembers().stream()
            .filter(TeamMember::isAccepted)
            .filter(teamMember -> teamMember.getUserId().value().equals(userId))
            .findFirst()
            .orElseThrow(() -> new BusinessException(errorCode));
    if (!artistVerificationRepository.isVerifiedArtist(userId)) {
      throw new BusinessException(errorCode);
    }
    return member;
  }

  private Map<Long, String> resolveCoAuthorNames(Display display, List<Long> coAuthorUserIds) {
    Map<Long, String> names = new LinkedHashMap<>();
    for (Long coAuthorUserId : coAuthorUserIds) {
      TeamMember member =
          requireVerifiedTeamMember(
              display, coAuthorUserId, DisplayArtworkErrorCode.INVALID_CO_AUTHOR);
      names.put(coAuthorUserId, member.getDisplayNickname());
    }
    return names;
  }
}
