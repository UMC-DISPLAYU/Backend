package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingLikeResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingLike;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtworkFeelingLikeService {

  private final ArtworkFeelingRepository artworkFeelingRepository;
  private final ArtworkFeelingLikeRepository artworkFeelingLikeRepository;
  private final ArtworkFeelingValidator artworkFeelingValidator;

  public ArtworkFeelingLikeResult like(ArtworkFeelingLikeCommand command) {
    validateFeeling(command);

    ArtworkFeelingLike feelingLike =
        artworkFeelingLikeRepository
            .findByFeelingIdAndUserId(command.feelingId(), command.userId())
            .map(this::restoreDeletedLike)
            .orElseGet(() -> ArtworkFeelingLike.create(command.feelingId(), command.userId()));

    try {
      feelingLike = artworkFeelingLikeRepository.save(feelingLike);
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE, exception);
    }

    return result(feelingLike, true);
  }

  public ArtworkFeelingLikeResult cancel(ArtworkFeelingLikeCommand command) {
    validateFeeling(command);

    ArtworkFeelingLike feelingLike =
        artworkFeelingLikeRepository
            .findByFeelingIdAndUserId(command.feelingId(), command.userId())
            .filter(like -> !like.isDeleted())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

    feelingLike.delete();

    return result(feelingLike, false);
  }

  private void validateFeeling(ArtworkFeelingLikeCommand command) {
    artworkFeelingValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkFeelingValidator.validateUserExists(command.userId());

    ArtworkFeeling artworkFeeling = findFeelingOrThrow(command.feelingId());
    artworkFeelingValidator.validateReplyTarget(artworkFeeling, command.displayArtworkId());
  }

  private ArtworkFeelingLike restoreDeletedLike(ArtworkFeelingLike feelingLike) {
    if (!feelingLike.isDeleted()) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE);
    }
    feelingLike.restore();
    return feelingLike;
  }

  private ArtworkFeelingLikeResult result(ArtworkFeelingLike feelingLike, boolean liked) {
    return new ArtworkFeelingLikeResult(
        feelingLike.getFeelingId(),
        liked,
        Math.toIntExact(
            artworkFeelingLikeRepository.countByFeelingIdAndDeletedAtIsNull(
                feelingLike.getFeelingId())),
        feelingLike.getCreatedAt(),
        feelingLike.getDeletedAt());
  }

  private ArtworkFeeling findFeelingOrThrow(Long feelingId) {
    return artworkFeelingRepository
        .findById(feelingId)
        .orElseThrow(() -> new BusinessException(ArtworkCommunicationErrorCode.FEELING_NOT_FOUND));
  }
}
