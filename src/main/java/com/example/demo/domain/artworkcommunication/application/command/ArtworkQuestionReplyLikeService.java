package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.permission.ArtworkCommunicationPermissionChecker;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionReplyLikeResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReplyLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyLikeRepository.ArtworkQuestionReplyLikeSnapshot;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtworkQuestionReplyLikeService {

  private final ArtworkQuestionReplyLikeRepository artworkQuestionReplyLikeRepository;
  private final ArtworkQuestionValidator artworkQuestionValidator;
  private final CreatorExistenceRepository creatorExistenceRepository;
  private final ArtworkCommunicationPermissionChecker permissionChecker;

  public ArtworkQuestionReplyLikeResult like(ArtworkQuestionReplyLikeCommand command) {
    validateReply(command);

    ArtworkQuestionReplyLike questionReplyLike =
        artworkQuestionReplyLikeRepository
            .findByQuestionReplyIdAndUserId(command.questionReplyId(), command.userId())
            .map(this::restoreDeletedLike)
            .orElseGet(
                () -> ArtworkQuestionReplyLike.create(command.questionReplyId(), command.userId()));

    try {
      questionReplyLike = artworkQuestionReplyLikeRepository.save(questionReplyLike);
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE, exception);
    }

    return result(questionReplyLike, true);
  }

  public ArtworkQuestionReplyLikeResult cancel(ArtworkQuestionReplyLikeCommand command) {
    validateReply(command);

    ArtworkQuestionReplyLike questionReplyLike =
        artworkQuestionReplyLikeRepository
            .findByQuestionReplyIdAndUserId(command.questionReplyId(), command.userId())
            .filter(like -> !like.isDeleted())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

    questionReplyLike.delete();

    return result(questionReplyLike, false);
  }

  private void validateReply(ArtworkQuestionReplyLikeCommand command) {
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

    ArtworkQuestionReply reply =
        artworkQuestionValidator.findActiveReplyForUpdateOrThrow(command.questionReplyId());
    artworkQuestionValidator.validateReplyTarget(reply, command.questionId());
  }

  private ArtworkQuestionReplyLike restoreDeletedLike(ArtworkQuestionReplyLike questionReplyLike) {
    if (!questionReplyLike.isDeleted()) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE);
    }
    questionReplyLike.restore();
    return questionReplyLike;
  }

  private ArtworkQuestionReplyLikeResult result(
      ArtworkQuestionReplyLike questionReplyLike, boolean liked) {
    return new ArtworkQuestionReplyLikeResult(
        questionReplyLike.getQuestionReplyId(),
        liked,
        artworkQuestionReplyLikeRepository.countByQuestionReplyIdAndDeletedAtIsNull(
            questionReplyLike.getQuestionReplyId()),
        questionReplyLike.getCreatedAt(),
        questionReplyLike.getDeletedAt());
  }
}
