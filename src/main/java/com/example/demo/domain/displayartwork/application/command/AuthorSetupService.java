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
import com.example.demo.domain.displayartwork.domain.repository.UserNicknameRepository;
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
  private final UserNicknameRepository userNicknameRepository;

  public AuthorSetupService(
      DisplayArtworkRepository displayArtworkRepository,
      CreatorRepository creatorRepository,
      ArtistVerificationRepository artistVerificationRepository,
      UserNicknameRepository userNicknameRepository) {
    this.displayArtworkRepository = displayArtworkRepository;
    this.creatorRepository = creatorRepository;
    this.artistVerificationRepository = artistVerificationRepository;
    this.userNicknameRepository = userNicknameRepository;
  }

  @Transactional
  public AuthorSetupResult setup(Long requesterUserId, AuthorSetupCommand command) {
    return apply(requesterUserId, command, true);
  }

  /**
   * 수정 시 작가 정보를 다시 저장한다. 수정 권한은 {@link ArtworkEditPermission}에서 이미 검증하므로, 등록 단계에만 해당하는 제한(요청자 작가
   * 인증, 대리 등록은 대표자만)은 적용하지 않는다.
   */
  @Transactional
  public AuthorSetupResult setupForUpdate(Long requesterUserId, AuthorSetupCommand command) {
    return apply(requesterUserId, command, false);
  }

  private AuthorSetupResult apply(
      Long requesterUserId, AuthorSetupCommand command, boolean isRegistration) {
    Objects.requireNonNull(command, "command must not be null.");

    DisplayArtwork artwork =
        displayArtworkRepository
            .findById(command.artworkId())
            .filter(a -> !a.isDeleted())
            .orElseThrow(
                () -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND));
    Display display = artwork.getDisplay();

    validateRequester(display, requesterUserId, isRegistration);

    Long artistUserId = command.artistUserId();
    if (artistUserId != null) {
      requireVerifiedParticipant(
          display, artistUserId, DisplayArtworkErrorCode.INVALID_ARTIST_USER_ID);
    }

    // 대리 등록(대표 작가가 본인이 아닌 경우)은 전시 대표자만 할 수 있다.
    if (isRegistration
        && !Objects.equals(artistUserId, requesterUserId)
        && !isDisplayLeader(display, requesterUserId)) {
      throw new BusinessException(DisplayArtworkErrorCode.FORBIDDEN_PROXY_ARTWORK_REGISTRATION);
    }

    // 계정 없는 작가를 대리 등록하면 artistUserId가 null이므로 공동 작업자 중복 검사에서 제외한다.
    List<Long> coAuthorUserIds = command.coAuthorUserIds();
    if ((artistUserId != null && coAuthorUserIds.contains(artistUserId))
        || new HashSet<>(coAuthorUserIds).size() != coAuthorUserIds.size()) {
      throw new BusinessException(DisplayArtworkErrorCode.INVALID_CO_AUTHOR);
    }
    Map<Long, String> coAuthorNames = resolveCoAuthorNames(display, coAuthorUserIds);

    // 작가(대표 작가/공동 작업자)는 Q&A 담당자가 될 수 있고,
    // 전시 대표자는 해당 작품의 작가인지와 무관하게 Q&A 담당자가 될 수 있다.
    // 계정이 없는 작가를 대리 등록하는 경우 후보가 대표자뿐인 상황도 정상이다.
    Set<Long> artistUserIds = new HashSet<>(coAuthorUserIds);
    if (artistUserId != null) {
      artistUserIds.add(artistUserId);
    }
    List<Long> qaHandlerUserIds = command.qaHandlerUserIds().stream().distinct().toList();
    for (Long qaHandlerUserId : qaHandlerUserIds) {
      if (!artistUserIds.contains(qaHandlerUserId) && !isDisplayLeader(display, qaHandlerUserId)) {
        throw new BusinessException(DisplayArtworkErrorCode.INVALID_QA_HANDLER);
      }
    }

    creatorRepository.deleteAllByDisplayArtworkId(command.artworkId());

    List<Creator> creators = new ArrayList<>();
    creators.add(
        new Creator(
            null,
            command.artistName(),
            qaHandlerUserIds.contains(artistUserId),
            true,
            artistUserId,
            command.artworkId()));
    for (Long coAuthorUserId : coAuthorUserIds) {
      creators.add(
          new Creator(
              null,
              coAuthorNames.get(coAuthorUserId),
              qaHandlerUserIds.contains(coAuthorUserId),
              false,
              coAuthorUserId,
              command.artworkId()));
    }
    for (String rawName : command.coAuthorRawNames()) {
      creators.add(new Creator(null, rawName, false, false, null, command.artworkId()));
    }
    // Q&A 답변 권한과 답변자 표기는 모두 Creator를 근거로 하므로, 작가가 아닌 전시 대표자를
    // 담당자로 지정한 경우에도 Creator를 남겨야 실제로 답변할 수 있다.
    for (Long qaHandlerUserId : qaHandlerUserIds) {
      if (!artistUserIds.contains(qaHandlerUserId)) {
        creators.add(
            new Creator(
                null,
                resolveDisplayLeaderName(display, qaHandlerUserId),
                true,
                false,
                qaHandlerUserId,
                command.artworkId()));
      }
    }
    creatorRepository.saveAll(creators);

    int coAuthorCount = coAuthorUserIds.size() + command.coAuthorRawNames().size();
    return new AuthorSetupResult(
        command.artworkId(), command.artistName(), artistUserId, coAuthorCount, qaHandlerUserIds);
  }

  /** 작가가 아닌 전시 대표자를 Creator로 남길 때 사용할 이름을 찾는다. */
  private String resolveDisplayLeaderName(Display display, Long userId) {
    return display.getTeamMembers().stream()
        .filter(TeamMember::isAccepted)
        .filter(teamMember -> teamMember.getUserId().value().equals(userId))
        .map(TeamMember::getDisplayNickname)
        .findFirst()
        .or(() -> userNicknameRepository.findNicknameById(userId))
        .orElseThrow(() -> new BusinessException(DisplayArtworkErrorCode.INVALID_QA_HANDLER));
  }

  /** 전시 대표자(전시 생성자 또는 수락된 팀장)인지 판별한다. */
  private boolean isDisplayLeader(Display display, Long userId) {
    return display.isOwner(userId) || display.isTeamLeader(userId);
  }

  private void validateRequester(
      Display display, Long requesterUserId, boolean requireVerifiedRequester) {
    // 전시를 만든 소유자는 TeamMember로 등록되지 않으므로 소유자 여부도 함께 확인한다.
    boolean isAcceptedTeamMember =
        display.isOwner(requesterUserId) || display.hasAcceptedTeamMember(requesterUserId);
    if (!isAcceptedTeamMember) {
      throw new BusinessException(DisplayArtworkErrorCode.NOT_DISPLAY_TEAM_MEMBER);
    }
    // 작가 인증은 작품을 새로 등록할 때 필요한 조건이다. 이미 등록된 작품을 관리하는 수정 경로에서는
    // 요구하지 않는다. 인증 없이도 전시 대표자가 될 수 있어, 요구하면 대표자가 수정하지 못한다.
    if (requireVerifiedRequester
        && !artistVerificationRepository.isVerifiedArtist(requesterUserId)) {
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
