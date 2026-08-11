package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingReplyLikeResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyLikeRepository.ArtworkFeelingReplyLikeSnapshot;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtworkFeelingReplyLikeService {

  private final ArtworkFeelingRepository artworkFeelingRepository;
  private final ArtworkFeelingReplyLikeRepository artworkFeelingReplyLikeRepository;
  private final ArtworkFeelingValidator artworkFeelingValidator;

  public ArtworkFeelingReplyLikeResult likeReply(ArtworkFeelingReplyLikeCommand command) {
    validateLikeTarget(command);

    ArtworkFeelingReplyLikeSnapshot snapshot =
        artworkFeelingReplyLikeRepository
            .likeAndGetSnapshot(command.feelingReplyId(), command.userId())
            .orElseThrow(
                () -> new BusinessException(ArtworkCommunicationErrorCode.FEELING_REPLY_NOT_FOUND));

    return toResult(snapshot);
  }

  public ArtworkFeelingReplyLikeResult cancelReplyLike(ArtworkFeelingReplyLikeCommand command) {
    validateLikeTarget(command);

    ArtworkFeelingReplyLikeSnapshot snapshot =
        artworkFeelingReplyLikeRepository
            .deleteAndGetSnapshot(command.feelingReplyId(), command.userId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        ArtworkCommunicationErrorCode.FEELING_REPLY_LIKE_NOT_FOUND));

    return toResult(snapshot);
  }

  private void validateLikeTarget(ArtworkFeelingReplyLikeCommand command) {
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

  private ArtworkFeelingReplyLikeResult toResult(ArtworkFeelingReplyLikeSnapshot snapshot) {
    return new ArtworkFeelingReplyLikeResult(
        snapshot.feelingReplyId(),
        snapshot.liked(),
        Math.toIntExact(snapshot.likeCount()),
        snapshot.createdAt(),
        snapshot.deletedAt());
  }
}
