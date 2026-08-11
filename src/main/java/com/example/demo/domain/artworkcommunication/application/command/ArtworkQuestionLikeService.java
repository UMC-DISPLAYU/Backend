package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.permission.ArtworkCommunicationPermissionChecker;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionLikeResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionLikeRepository.ArtworkQuestionLikeSnapshot;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtworkQuestionLikeService {

  private final ArtworkQuestionLikeRepository artworkQuestionLikeRepository;
  private final ArtworkQuestionValidator artworkQuestionValidator;
  private final CreatorExistenceRepository creatorExistenceRepository;
  private final ArtworkCommunicationPermissionChecker permissionChecker;

  public ArtworkQuestionLikeResult likeQuestion(ArtworkQuestionLikeCommand command) {
    validateLikeTarget(command);

    ArtworkQuestionLikeSnapshot snapshot =
        artworkQuestionLikeRepository
            .likeAndGetSnapshot(command.questionId(), command.userId())
            .orElseThrow(
                () -> new BusinessException(ArtworkCommunicationErrorCode.QUESTION_NOT_FOUND));

    return toResult(snapshot);
  }

  public ArtworkQuestionLikeResult cancelQuestionLike(ArtworkQuestionLikeCommand command) {
    validateLikeTarget(command);

    ArtworkQuestionLikeSnapshot snapshot =
        artworkQuestionLikeRepository
            .deleteAndGetSnapshot(command.questionId(), command.userId())
            .orElseThrow(
                () -> new BusinessException(ArtworkCommunicationErrorCode.QUESTION_LIKE_NOT_FOUND));

    return toResult(snapshot);
  }

  private void validateLikeTarget(ArtworkQuestionLikeCommand command) {
    artworkQuestionValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkQuestionValidator.validateUserExists(command.userId());

    ArtworkQuestion question =
        artworkQuestionValidator.findActiveQuestionForUpdateOrThrow(command.questionId());
    artworkQuestionValidator.validateQuestionTarget(question, command.displayArtworkId());
    boolean isParticipant =
        creatorExistenceRepository
            .findParticipantNameByDisplayArtworkIdAndUserId(
                command.displayArtworkId(), command.userId())
            .isPresent();
    permissionChecker.requireQuestionAccessible(question, command.userId(), isParticipant);
  }

  private ArtworkQuestionLikeResult toResult(ArtworkQuestionLikeSnapshot snapshot) {
    return new ArtworkQuestionLikeResult(
        snapshot.questionId(),
        snapshot.liked(),
        snapshot.likeCount(),
        snapshot.createdAt(),
        snapshot.deletedAt());
  }
}
