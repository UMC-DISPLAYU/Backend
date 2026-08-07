package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingReplyLikeResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReplyLike;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyLikeRepository;
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
public class ArtworkFeelingReplyLikeService {

  private final ArtworkFeelingRepository artworkFeelingRepository;
  private final ArtworkFeelingReplyLikeRepository artworkFeelingReplyLikeRepository;
  private final ArtworkFeelingValidator artworkFeelingValidator;

  public ArtworkFeelingReplyLikeResult like(ArtworkFeelingReplyLikeCommand command) {
    validateReply(command);

    ArtworkFeelingReplyLike replyLike =
        artworkFeelingReplyLikeRepository
            .findByFeelingReplyIdAndUserId(command.feelingReplyId(), command.userId())
            .map(this::restoreDeletedLike)
            .orElseGet(
                () -> ArtworkFeelingReplyLike.create(command.feelingReplyId(), command.userId()));

    try {
      replyLike = artworkFeelingReplyLikeRepository.save(replyLike);
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE, exception);
    }

    return result(replyLike, true);
  }

  public ArtworkFeelingReplyLikeResult cancel(ArtworkFeelingReplyLikeCommand command) {
    validateReply(command);

    ArtworkFeelingReplyLike replyLike =
        artworkFeelingReplyLikeRepository
            .findByFeelingReplyIdAndUserId(command.feelingReplyId(), command.userId())
            .filter(like -> !like.isDeleted())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

    replyLike.delete();

    return result(replyLike, false);
  }

  private void validateReply(ArtworkFeelingReplyLikeCommand command) {
    artworkFeelingValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkFeelingValidator.validateUserExists(command.userId());

    ArtworkFeeling artworkFeeling =
        artworkFeelingRepository
            .findById(command.feelingId())
            .orElseThrow(
                () -> new BusinessException(ArtworkCommunicationErrorCode.FEELING_NOT_FOUND));
    artworkFeelingValidator.validateReplyTarget(artworkFeeling, command.displayArtworkId());

    ArtworkFeelingReply reply =
        artworkFeelingValidator.findActiveReplyForUpdateOrThrow(command.feelingReplyId());
    artworkFeelingValidator.validateReplyTarget(reply, command.feelingId());
  }

  private ArtworkFeelingReplyLike restoreDeletedLike(ArtworkFeelingReplyLike replyLike) {
    if (!replyLike.isDeleted()) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE);
    }
    replyLike.restore();
    return replyLike;
  }

  private ArtworkFeelingReplyLikeResult result(ArtworkFeelingReplyLike replyLike, boolean liked) {
    return new ArtworkFeelingReplyLikeResult(
        replyLike.getFeelingReplyId(),
        liked,
        Math.toIntExact(
            artworkFeelingReplyLikeRepository.countByFeelingReplyIdAndDeletedAtIsNull(
                replyLike.getFeelingReplyId())),
        replyLike.getCreatedAt(),
        replyLike.getDeletedAt());
  }
}
