package com.example.demo.domain.displayartwork.application.query;

import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.displayartwork.application.permission.DisplayArtworkPermissionChecker;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkByArtistResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkDetailResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkDetailResult.CoAuthorResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkDetailResult.QaHandlerResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkEditResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkListResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkListResult.ArtworkItemResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkPreviewResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkPreviewResult.ArtworkCardResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkPreviewResult.ExhibitionInfoResult;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.entity.Creator;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.CreatorRepository;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkLikeRepository;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displayartwork.domain.type.DisplayArtworkStatus;
import com.example.demo.domain.displayartwork.domain.type.PreviewFilterType;
import com.example.demo.global.error.BusinessException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisplayArtworkQueryService {

  private final DisplayRepository displayRepository;
  private final DisplayArtworkRepository displayArtworkRepository;
  private final CreatorRepository creatorRepository;
  private final DisplayArtworkLikeRepository displayArtworkLikeRepository;
  private final ArchiveWorkRepository archiveWorkRepository;
  private final DisplayArtworkPermissionChecker permissionChecker;

  public DisplayArtworkQueryService(
      DisplayRepository displayRepository,
      DisplayArtworkRepository displayArtworkRepository,
      CreatorRepository creatorRepository,
      DisplayArtworkLikeRepository displayArtworkLikeRepository,
      ArchiveWorkRepository archiveWorkRepository,
      DisplayArtworkPermissionChecker permissionChecker) {
    this.displayRepository = displayRepository;
    this.displayArtworkRepository = displayArtworkRepository;
    this.creatorRepository = creatorRepository;
    this.displayArtworkLikeRepository = displayArtworkLikeRepository;
    this.archiveWorkRepository = archiveWorkRepository;
    this.permissionChecker = permissionChecker;
  }

  @Transactional(readOnly = true)
  public DisplayArtworkDetailResult getDisplayArtworkFullDetail(
      Long displayArtworkId, Long requesterUserId) {
    DisplayArtwork displayArtwork =
        displayArtworkRepository
            .findById(displayArtworkId)
            .filter(artwork -> !artwork.isDeleted() && !artwork.getDisplay().isDeleted())
            .orElseThrow(
                () -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND));
    if (!canViewDraft(displayArtwork.getDisplay(), requesterUserId)
        && (displayArtwork.getStatus() != DisplayArtworkStatus.PUBLISHED
            || displayArtwork.getDisplay().getStatus() != DisplayStatus.PUBLISHED)) {
      throw new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND);
    }

    List<Creator> creators = creatorRepository.findByDisplayArtworkId(displayArtworkId);
    Optional<Creator> leader = creators.stream().filter(Creator::isLeader).findFirst();
    String artistName = leader.map(Creator::getCreatorName).orElse(null);
    Long artistUserId = leader.map(Creator::getUserId).orElse(null);
    // QnA 담당자로만 지정된 전시 대표자는 작품의 작가가 아니므로 공동 작업자 목록에서 제외한다.
    // 계정 없이 이름만 입력한 공동 작업자는 userId가 null로 내려가고, 프론트는 이 값으로 프로필 이동 가능 여부를 판단한다.
    List<CoAuthorResult> coAuthors =
        creators.stream()
            .filter(Creator::isCoAuthor)
            .map(creator -> new CoAuthorResult(creator.getUserId(), creator.getCreatorName()))
            .toList();
    List<QaHandlerResult> qaHandlers =
        creators.stream()
            .filter(Creator::isContact)
            .map(creator -> new QaHandlerResult(creator.getUserId(), creator.getCreatorName()))
            .toList();

    long likeCount = displayArtworkLikeRepository.countByDisplayArtworkId(displayArtworkId);
    // 비회원 조회를 허용하므로 requesterUserId가 없으면 사용자별 상태는 모두 false로 둔다.
    boolean isLiked =
        requesterUserId != null
            && displayArtworkLikeRepository.existsByDisplayArtworkIdAndUserId(
                displayArtworkId, requesterUserId);
    boolean isArchived =
        requesterUserId != null
            && archiveWorkRepository
                .findByUserIdAndDisplayArtworkId(requesterUserId, displayArtworkId)
                .isPresent();

    return DisplayArtworkDetailResult.of(
        displayArtwork,
        artistName,
        artistUserId,
        coAuthors,
        qaHandlers,
        likeCount,
        isLiked,
        isArchived);
  }

  /** 수정 화면 진입 시 등록 당시 상태를 그대로 복원하기 위해 공동 작업자까지 포함해 조회한다. */
  @Transactional(readOnly = true)
  public DisplayArtworkEditResult getArtworkForEdit(Long displayArtworkId, Long requesterUserId) {
    DisplayArtwork displayArtwork =
        displayArtworkRepository
            .findById(displayArtworkId)
            .filter(artwork -> !artwork.isDeleted() && !artwork.getDisplay().isDeleted())
            .orElseThrow(
                () -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND));

    permissionChecker.requireArtworkEditor(
        requesterUserId, displayArtwork.getDisplay(), displayArtworkId);

    List<Creator> creators = creatorRepository.findByDisplayArtworkId(displayArtworkId);
    Optional<Creator> leader = creators.stream().filter(Creator::isLeader).findFirst();
    // QnA 담당자로만 지정된 전시 대표자도 Creator로 남아 있어 !isLeader로는 걸러지지 않는다. role로 판단한다.
    List<DisplayArtworkEditResult.CoAuthorResult> coAuthors =
        creators.stream()
            .filter(Creator::isCoAuthor)
            .map(
                creator ->
                    new DisplayArtworkEditResult.CoAuthorResult(
                        creator.getUserId(), creator.getCreatorName()))
            .toList();
    List<Long> qaHandlerUserIds =
        creators.stream()
            .filter(Creator::isContact)
            .map(Creator::getUserId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();

    return DisplayArtworkEditResult.of(
        displayArtwork,
        leader.map(Creator::getCreatorName).orElse(null),
        leader.map(Creator::getUserId).orElse(null),
        coAuthors,
        qaHandlerUserIds);
  }

  private static final DateTimeFormatter FULL_DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd");
  private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("MM.dd");

  @Transactional(readOnly = true)
  public DisplayArtworkPreviewResult getPreview(
      PreviewFilterType type, ArtworkType field, String school, int page, int size) {
    List<DisplayArtwork> fetched =
        displayArtworkRepository.findPreview(type, field, school, page, size);

    boolean isLast = fetched.size() <= size;
    List<DisplayArtwork> pageItems = isLast ? fetched : fetched.subList(0, size);

    Map<Long, String> artistNamesByArtworkId =
        creatorRepository
            .findLeadersByDisplayArtworkIds(pageItems.stream().map(DisplayArtwork::getId).toList())
            .stream()
            .collect(Collectors.toMap(Creator::getDisplayArtworkId, Creator::getCreatorName));

    List<ArtworkCardResult> cards =
        pageItems.stream().map(artwork -> toCard(artwork, artistNamesByArtworkId)).toList();
    return new DisplayArtworkPreviewResult(cards, page, size, isLast);
  }

  private ArtworkCardResult toCard(
      DisplayArtwork displayArtwork, Map<Long, String> artistNamesByArtworkId) {
    ArtworkImage thumbnail = findThumbnail(displayArtwork);
    var display = displayArtwork.getDisplay();
    var period = display.getPeriod();
    String formattedPeriod =
        "%s - %s"
            .formatted(period.startDate().format(FULL_DATE), period.endDate().format(SHORT_DATE));

    return new ArtworkCardResult(
        displayArtwork.getId(),
        displayArtwork.getArtworkName(),
        artistNamesByArtworkId.get(displayArtwork.getId()),
        thumbnail != null ? thumbnail.getImageUrl() : null,
        thumbnail != null ? thumbnail.getWidth() : 0,
        thumbnail != null ? thumbnail.getHeight() : 0,
        new ExhibitionInfoResult(
            display.getId(),
            display.getTitle(),
            formattedPeriod,
            display.getLocation().placeName()));
  }

  @Transactional(readOnly = true)
  public DisplayArtworkListResult getArtworksByDisplayId(Long displayId, Long requesterUserId) {
    var display =
        displayRepository
            .findById(displayId)
            .filter(candidate -> !candidate.isDeleted())
            .orElseThrow(() -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_NOT_FOUND));

    List<DisplayArtwork> artworks =
        findVisibleArtworks(display, requesterUserId).stream()
            .sorted(Comparator.comparing(DisplayArtwork::getWorkSortOrder))
            .toList();

    Map<Long, String> artistNamesByArtworkId =
        creatorRepository
            .findLeadersByDisplayArtworkIds(artworks.stream().map(DisplayArtwork::getId).toList())
            .stream()
            .collect(Collectors.toMap(Creator::getDisplayArtworkId, Creator::getCreatorName));

    List<ArtworkItemResult> items =
        artworks.stream().map(artwork -> toItem(artwork, artistNamesByArtworkId)).toList();
    return new DisplayArtworkListResult(items);
  }

  /**
   * 작가 프로필 - 작품 탭. 대표 작가/공동 작업자 구분 없이 해당 유저가 참여한 출품작을 등록순으로 조회한다. 공개 프로필이므로 공개된 전시의 공개된 작품만 노출한다.
   */
  @Transactional(readOnly = true)
  public DisplayArtworkByArtistResult getArtworksByUserId(Long userId) {
    List<DisplayArtwork> artworks = displayArtworkRepository.findAllByParticipantUserId(userId);

    Map<Long, String> artistNamesByArtworkId =
        creatorRepository
            .findLeadersByDisplayArtworkIds(artworks.stream().map(DisplayArtwork::getId).toList())
            .stream()
            .collect(Collectors.toMap(Creator::getDisplayArtworkId, Creator::getCreatorName));

    List<DisplayArtworkByArtistResult.ArtworkCardResult> cards =
        artworks.stream().map(artwork -> toArtistCard(artwork, artistNamesByArtworkId)).toList();
    return new DisplayArtworkByArtistResult(cards);
  }

  private DisplayArtworkByArtistResult.ArtworkCardResult toArtistCard(
      DisplayArtwork displayArtwork, Map<Long, String> artistNamesByArtworkId) {
    ArtworkImage thumbnail = findThumbnail(displayArtwork);
    var display = displayArtwork.getDisplay();
    var period = display.getPeriod();
    String formattedPeriod =
        "%s - %s"
            .formatted(period.startDate().format(FULL_DATE), period.endDate().format(SHORT_DATE));

    return new DisplayArtworkByArtistResult.ArtworkCardResult(
        displayArtwork.getId(),
        displayArtwork.getArtworkName(),
        artistNamesByArtworkId.get(displayArtwork.getId()),
        thumbnail != null ? thumbnail.getImageUrl() : null,
        thumbnail != null ? thumbnail.getWidth() : 0,
        thumbnail != null ? thumbnail.getHeight() : 0,
        displayArtwork.getCreatedAt(),
        new DisplayArtworkByArtistResult.ExhibitionInfoResult(
            display.getId(),
            display.getTitle(),
            formattedPeriod,
            display.getLocation().placeName()));
  }

  private List<DisplayArtwork> findVisibleArtworks(Display display, Long requesterUserId) {
    if (display.isDeleted()) {
      throw new BusinessException(DisplayArtworkErrorCode.DISPLAY_NOT_FOUND);
    }
    if (canViewDraft(display, requesterUserId)) {
      return displayArtworkRepository.findAllByDisplayId(display.getId());
    }
    if (display.getStatus() != DisplayStatus.PUBLISHED) {
      throw new BusinessException(DisplayArtworkErrorCode.DISPLAY_NOT_FOUND);
    }
    return displayArtworkRepository.findPublishedByDisplayId(display.getId());
  }

  private boolean canViewDraft(Display display, Long requesterUserId) {
    return requesterUserId != null
        && (display.isOwner(requesterUserId) || display.hasAcceptedTeamMember(requesterUserId));
  }

  private ArtworkItemResult toItem(
      DisplayArtwork displayArtwork, Map<Long, String> artistNamesByArtworkId) {
    ArtworkImage thumbnail = findThumbnail(displayArtwork);
    return new ArtworkItemResult(
        displayArtwork.getId(),
        displayArtwork.getArtworkName(),
        artistNamesByArtworkId.get(displayArtwork.getId()),
        thumbnail != null ? thumbnail.getImageUrl() : null,
        thumbnail != null ? thumbnail.getWidth() : 0,
        thumbnail != null ? thumbnail.getHeight() : 0);
  }

  private ArtworkImage findThumbnail(DisplayArtwork displayArtwork) {
    return displayArtwork.getImages().stream()
        .sorted(Comparator.comparing(ArtworkImage::isThumbnail).reversed())
        .findFirst()
        .orElse(null);
  }
}
