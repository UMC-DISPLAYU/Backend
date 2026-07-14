package com.example.demo.domain.displayartwork.application.query;

import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkDetailResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkResult;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.Creator;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.CreatorRepository;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkLikeRepository;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Optional;
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
  public DisplayArtworkResult getDisplayArtworkDetail(Long displayArtworkId) {
    return displayArtworkRepository
        .findById(displayArtworkId)
        .filter(artwork -> !artwork.isDeleted())
        .map(DisplayArtworkResult::from)
        .orElseThrow(
            () -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND));
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

    long likeCount = displayArtworkLikeRepository.countByDisplayArtworkId(displayArtworkId);
    boolean isLiked =
        displayArtworkLikeRepository.existsByDisplayArtworkIdAndUserId(
            displayArtworkId, requesterUserId);
    boolean isSaved =
        archiveWorkRepository
            .findByUserIdAndDisplayArtworkId(requesterUserId, displayArtworkId)
            .isPresent();

    return DisplayArtworkDetailResult.of(
        displayArtwork, artistName, artistUserId, likeCount, isLiked, isSaved);
  }
}
