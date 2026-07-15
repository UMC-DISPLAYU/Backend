package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingLikeResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingLikeRepository.ArtworkFeelingLikeSnapshot;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtworkFeelingLikeService {

  private final ArtworkFeelingRepository artworkFeelingRepository;
  private final ArtworkFeelingLikeRepository artworkFeelingLikeRepository;
  private final ArtworkFeelingValidator artworkFeelingValidator;

  public ArtworkFeelingLikeResult artworkFeelingLike(ArtworkFeelingLikeCommand command) {
    artworkFeelingValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkFeelingValidator.validateUserExists(command.userId());

    ArtworkFeeling artworkFeeling = findFeelingOrThrow(command.feelingId());
    artworkFeelingValidator.validateReplyTarget(artworkFeeling, command.displayArtworkId());

    ArtworkFeelingLikeSnapshot snapshot =
        artworkFeelingLikeRepository
            .toggleAndGetSnapshot(command.feelingId(), command.userId())
            .orElseThrow(
                () -> new BusinessException(ArtworkCommunicationErrorCode.FEELING_NOT_FOUND));

    return new ArtworkFeelingLikeResult(
        snapshot.feelingId(),
        snapshot.liked(),
        Math.toIntExact(snapshot.likeCount()),
        snapshot.createdAt(),
        snapshot.deletedAt());
  }

  private ArtworkFeeling findFeelingOrThrow(Long feelingId) {
    return artworkFeelingRepository
        .findById(feelingId)
        .orElseThrow(() -> new BusinessException(ArtworkCommunicationErrorCode.FEELING_NOT_FOUND));
  }
}
