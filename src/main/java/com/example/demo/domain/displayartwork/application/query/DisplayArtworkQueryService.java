package com.example.demo.domain.displayartwork.application.query;

import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkDetailResult;
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
import com.example.demo.domain.displayartwork.domain.type.PreviewFilterType;
import com.example.demo.global.error.BusinessException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisplayArtworkQueryService {

  private final DisplayArtworkRepository displayArtworkRepository;
  private final CreatorRepository creatorRepository;
  private final DisplayArtworkLikeRepository displayArtworkLikeRepository;
  private final ArchiveWorkRepository archiveWorkRepository;

  public DisplayArtworkQueryService(
      DisplayArtworkRepository displayArtworkRepository,
      CreatorRepository creatorRepository,
      DisplayArtworkLikeRepository displayArtworkLikeRepository,
      ArchiveWorkRepository archiveWorkRepository) {
    this.displayArtworkRepository = displayArtworkRepository;
    this.creatorRepository = creatorRepository;
    this.displayArtworkLikeRepository = displayArtworkLikeRepository;
    this.archiveWorkRepository = archiveWorkRepository;
  }

  @Transactional(readOnly = true)
  public DisplayArtworkDetailResult getDisplayArtworkFullDetail(
      Long displayArtworkId, Long requesterUserId) {
    DisplayArtwork displayArtwork =
        displayArtworkRepository
            .findById(displayArtworkId)
            .filter(artwork -> !artwork.isDeleted())
            .orElseThrow(
                () -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND));

    Optional<Creator> leader = creatorRepository.findLeaderByDisplayArtworkId(displayArtworkId);
    String artistName = leader.map(Creator::getCreatorName).orElse(null);
    Long artistUserId = leader.map(Creator::getUserId).orElse(null);

    long likeCount =
        displayArtworkLikeRepository.countByDisplayArtworkIdAndDeletedAtIsNull(displayArtworkId);
    boolean isLiked =
        displayArtworkLikeRepository.existsByDisplayArtworkIdAndUserIdAndDeletedAtIsNull(
            displayArtworkId, requesterUserId);
    boolean isSaved =
        archiveWorkRepository
            .findByUserIdAndDisplayArtworkId(requesterUserId, displayArtworkId)
            .isPresent();

    return DisplayArtworkDetailResult.of(
        displayArtwork, artistName, artistUserId, likeCount, isLiked, isSaved);
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

  private ArtworkImage findThumbnail(DisplayArtwork displayArtwork) {
    return displayArtwork.getImages().stream()
        .sorted(Comparator.comparing(ArtworkImage::isThumbnail).reversed())
        .findFirst()
        .orElse(null);
  }
}
