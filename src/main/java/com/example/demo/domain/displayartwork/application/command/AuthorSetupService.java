package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.displayartwork.application.permission.DisplayArtworkPermissionChecker;
import com.example.demo.domain.displayartwork.application.result.AuthorSetupResult;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.Creator;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.CreatorRepository;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.domain.displayartwork.domain.repository.UserNicknameRepository;
import com.example.demo.domain.displayartwork.domain.type.CreatorRole;
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

  // 작품 조회
  private final DisplayArtworkRepository displayArtworkRepository;
  // Creator 행 저장/삭제
  private final CreatorRepository creatorRepository;
  // userId → 닉네임 조회 (Creator 이름 채울 때 씀)
  private final UserNicknameRepository userNicknameRepository;
  private final DisplayArtworkPermissionChecker permissionChecker;

  public AuthorSetupService(
      DisplayArtworkRepository displayArtworkRepository,
      CreatorRepository creatorRepository,
      UserNicknameRepository userNicknameRepository,
      DisplayArtworkPermissionChecker permissionChecker) {
    this.displayArtworkRepository = displayArtworkRepository;
    this.creatorRepository = creatorRepository;
    this.userNicknameRepository = userNicknameRepository;
    this.permissionChecker = permissionChecker;
  }

  @Transactional
  public AuthorSetupResult setup(Long requesterUserId, AuthorSetupCommand command) {
    return apply(requesterUserId, command, true);
  }

  /**
   * 수정 시 작가 정보를 다시 저장한다. 수정 권한은 {@link DisplayArtworkPermissionChecker}에서 이미 검증하므로, 등록 단계에만 해당하는
   * 제한(요청자 작가 인증, 대리 등록은 대표자만)은 적용하지 않는다.
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
            .filter(a -> !a.isDeleted() && !a.getDisplay().isDeleted())
            .orElseThrow(
                () -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND));
    Display display = artwork.getDisplay();

    if (isRegistration) {
      permissionChecker.requireArtworkRegistrant(requesterUserId, display);
    } else {
      permissionChecker.requireArtworkParticipant(requesterUserId, display);
    }

    Long artistUserId = command.artistUserId();
    if (artistUserId != null) {
      permissionChecker.requireVerifiedArtistParticipant(
          display, artistUserId, DisplayArtworkErrorCode.INVALID_ARTIST_USER_ID);
    }

    // 대리 등록(대표 작가가 본인이 아닌 경우)은 전시 대표자만 할 수 있다.
    if (isRegistration) {
      permissionChecker.requireProxyRegistrationAllowed(requesterUserId, display, artistUserId);
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
      permissionChecker.requireQnaHandlerAssignable(display, qaHandlerUserId, artistUserIds);
    }

    creatorRepository.deleteAllByDisplayArtworkId(command.artworkId());

    List<Creator> creators = new ArrayList<>();
    creators.add(
        new Creator(
            null,
            command.artistName(),
            qaHandlerUserIds.contains(artistUserId),
            CreatorRole.LEAD_ARTIST,
            artistUserId,
            command.artworkId()));
    for (Long coAuthorUserId : coAuthorUserIds) {
      creators.add(
          new Creator(
              null,
              coAuthorNames.get(coAuthorUserId),
              qaHandlerUserIds.contains(coAuthorUserId),
              CreatorRole.CO_AUTHOR,
              coAuthorUserId,
              command.artworkId()));
    }
    for (String rawName : command.coAuthorRawNames()) {
      creators.add(
          new Creator(null, rawName, false, CreatorRole.CO_AUTHOR, null, command.artworkId()));
    }
    // Q&A 답변 권한과 답변자 표기는 모두 Creator를 근거로 하므로, 작가가 아닌 전시 대표자를
    // 담당자로 지정한 경우에도 Creator를 남겨야 실제로 답변할 수 있다.
    // 다만 이 사람은 작품의 작가가 아니므로 QA_ONLY로 표시해 작가 목록 조회에서 제외한다.
    for (Long qaHandlerUserId : qaHandlerUserIds) {
      if (!artistUserIds.contains(qaHandlerUserId)) {
        creators.add(
            new Creator(
                null,
                resolveDisplayLeaderName(display, qaHandlerUserId),
                true,
                CreatorRole.QA_ONLY,
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

  private Map<Long, String> resolveCoAuthorNames(Display display, List<Long> coAuthorUserIds) {
    Map<Long, String> names = new LinkedHashMap<>();
    for (Long coAuthorUserId : coAuthorUserIds) {
      TeamMember member =
          permissionChecker.requireVerifiedArtistTeamMember(
              display, coAuthorUserId, DisplayArtworkErrorCode.INVALID_CO_AUTHOR);
      names.put(coAuthorUserId, member.getDisplayNickname());
    }
    return names;
  }
}
