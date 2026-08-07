package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionLikeResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionLikeRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalArtworkQuestionLikeService {

  private final PersonalArtworkQuestionLikeRepository personalArtworkQuestionLikeRepository;
  private final PersonalArtworkQuestionValidator personalArtworkQuestionValidator;

  public PersonalArtworkQuestionLikeResult like(PersonalArtworkQuestionLikeCommand command) {
    validateQuestion(command);

    PersonalArtworkQuestionLike questionLike =
        personalArtworkQuestionLikeRepository
            .findByPersonalQuestionIdAndUserId(command.personalQuestionId(), command.userId())
            .map(this::restoreDeletedLike)
            .orElseGet(
                () ->
                    PersonalArtworkQuestionLike.create(
                        command.personalQuestionId(), command.userId()));

    try {
      questionLike = personalArtworkQuestionLikeRepository.save(questionLike);
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE, exception);
    }

    return result(questionLike, true);
  }

  public PersonalArtworkQuestionLikeResult cancel(PersonalArtworkQuestionLikeCommand command) {
    validateQuestion(command);

    PersonalArtworkQuestionLike questionLike =
        personalArtworkQuestionLikeRepository
            .findByPersonalQuestionIdAndUserId(command.personalQuestionId(), command.userId())
            .filter(like -> !like.isDeleted())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

    questionLike.delete();

    return result(questionLike, false);
  }

  private void validateQuestion(PersonalArtworkQuestionLikeCommand command) {
    personalArtworkQuestionValidator.validatePersonalArtworkExists(command.personalArtworkId());
    personalArtworkQuestionValidator.validateUserExists(command.userId());

    PersonalArtworkQuestion question =
        personalArtworkQuestionValidator.findActiveQuestionForUpdateOrThrow(
            command.personalQuestionId());
    personalArtworkQuestionValidator.validateQuestionTarget(question, command.personalArtworkId());
    personalArtworkQuestionValidator.validateLikePermission(
        question, command.personalArtworkId(), command.userId());
  }

  private PersonalArtworkQuestionLike restoreDeletedLike(PersonalArtworkQuestionLike questionLike) {
    if (!questionLike.isDeleted()) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE);
    }
    questionLike.restore();
    return questionLike;
  }

  private PersonalArtworkQuestionLikeResult result(
      PersonalArtworkQuestionLike questionLike, boolean liked) {
    return new PersonalArtworkQuestionLikeResult(
        questionLike.getPersonalQuestionId(),
        liked,
        personalArtworkQuestionLikeRepository.countByPersonalQuestionIdAndDeletedAtIsNull(
            questionLike.getPersonalQuestionId()),
        questionLike.getCreatedAt(),
        questionLike.getDeletedAt());
  }
}
