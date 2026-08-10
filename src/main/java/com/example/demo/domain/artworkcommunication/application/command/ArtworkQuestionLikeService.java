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

  public ArtworkQuestionLikeResult toggleQuestionLike(ArtworkQuestionLikeCommand command) {
    artworkQuestionValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkQuestionValidator.validateUserExists(command.userId());

    ArtworkQuestion question =
        artworkQuestionValidator.findActiveQuestionForUpdateOrThrow(command.questionId());
    artworkQuestionValidator.validateQuestionTarget(question, command.displayArtworkId());
    boolean isCreatorOrHandler =
        creatorExistenceRepository
            .findCreatorNameByDisplayArtworkIdAndUserId(
                command.displayArtworkId(), command.userId())
            .isPresent();
    permissionChecker.requireQuestionAccessible(question, command.userId(), isCreatorOrHandler);

    ArtworkQuestionLikeSnapshot snapshot =
        artworkQuestionLikeRepository
            .toggleAndGetSnapshot(command.questionId(), command.userId())
            .orElseThrow(
                () -> new BusinessException(ArtworkCommunicationErrorCode.QUESTION_NOT_FOUND));

    return new ArtworkQuestionLikeResult(
        snapshot.questionId(),
        snapshot.liked(),
        snapshot.likeCount(),
        snapshot.createdAt(),
        snapshot.deletedAt());
  }
}
