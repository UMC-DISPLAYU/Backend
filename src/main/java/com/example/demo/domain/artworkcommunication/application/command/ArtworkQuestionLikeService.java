package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionLikeResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionLikeRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtworkQuestionLikeService {

  private final ArtworkQuestionLikeRepository artworkQuestionLikeRepository;
  private final ArtworkQuestionValidator artworkQuestionValidator;

  public ArtworkQuestionLikeResult like(ArtworkQuestionLikeCommand command) {
    validateQuestion(command);

    ArtworkQuestionLike questionLike =
        artworkQuestionLikeRepository
            .findByQuestionIdAndUserId(command.questionId(), command.userId())
            .map(this::restoreDeletedLike)
            .orElseGet(() -> ArtworkQuestionLike.create(command.questionId(), command.userId()));

    try {
      questionLike = artworkQuestionLikeRepository.save(questionLike);
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE, exception);
    }

    return result(questionLike, true);
  }

  public ArtworkQuestionLikeResult cancel(ArtworkQuestionLikeCommand command) {
    validateQuestion(command);

    ArtworkQuestionLike questionLike =
        artworkQuestionLikeRepository
            .findByQuestionIdAndUserId(command.questionId(), command.userId())
            .filter(like -> !like.isDeleted())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

    questionLike.delete();

    return result(questionLike, false);
  }

  private void validateQuestion(ArtworkQuestionLikeCommand command) {
    artworkQuestionValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkQuestionValidator.validateUserExists(command.userId());

    ArtworkQuestion question =
        artworkQuestionValidator.findActiveQuestionForUpdateOrThrow(command.questionId());
    artworkQuestionValidator.validateQuestionTarget(question, command.displayArtworkId());
    artworkQuestionValidator.validateLikePermission(
        question, command.displayArtworkId(), command.userId());
  }

  private ArtworkQuestionLike restoreDeletedLike(ArtworkQuestionLike questionLike) {
    if (!questionLike.isDeleted()) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE);
    }
    questionLike.restore();
    return questionLike;
  }

  private ArtworkQuestionLikeResult result(ArtworkQuestionLike questionLike, boolean liked) {
    return new ArtworkQuestionLikeResult(
        questionLike.getQuestionId(),
        liked,
        artworkQuestionLikeRepository.countByQuestionIdAndDeletedAtIsNull(
            questionLike.getQuestionId()),
        questionLike.getCreatedAt(),
        questionLike.getDeletedAt());
  }
}
