package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionReplyLikeResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReplyLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyLikeRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalArtworkQuestionReplyLikeService {

  private final PersonalArtworkQuestionReplyLikeRepository repository;
  private final PersonalArtworkQuestionValidator validator;

  public PersonalArtworkQuestionReplyLikeResult like(
      PersonalArtworkQuestionReplyLikeCommand command) {
    validateReply(command);

    PersonalArtworkQuestionReplyLike replyLike =
        repository
            .findByPersonalQuestionReplyIdAndUserId(
                command.personalQuestionReplyId(), command.userId())
            .map(this::restoreDeletedLike)
            .orElseGet(
                () ->
                    PersonalArtworkQuestionReplyLike.create(
                        command.personalQuestionReplyId(), command.userId()));

    try {
      replyLike = repository.save(replyLike);
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE, exception);
    }

    return result(replyLike, true);
  }

  public PersonalArtworkQuestionReplyLikeResult cancel(
      PersonalArtworkQuestionReplyLikeCommand command) {
    validateReply(command);

    PersonalArtworkQuestionReplyLike replyLike =
        repository
            .findByPersonalQuestionReplyIdAndUserId(
                command.personalQuestionReplyId(), command.userId())
            .filter(like -> !like.isDeleted())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

    replyLike.delete();

    return result(replyLike, false);
  }

  private void validateReply(PersonalArtworkQuestionReplyLikeCommand command) {
    validator.validatePersonalArtworkExists(command.personalArtworkId());
    validator.validateUserExists(command.userId());

    PersonalArtworkQuestion question =
        validator.findActiveQuestionForUpdateOrThrow(command.personalQuestionId());
    validator.validateQuestionTarget(question, command.personalArtworkId());
    validator.validateLikePermission(question, command.personalArtworkId(), command.userId());

    PersonalArtworkQuestionReply reply =
        validator.findActiveReplyForUpdateOrThrow(command.personalQuestionReplyId());
    validator.validateReplyBelongsToQuestion(reply, command.personalQuestionId());
  }

  private PersonalArtworkQuestionReplyLike restoreDeletedLike(
      PersonalArtworkQuestionReplyLike replyLike) {
    if (!replyLike.isDeleted()) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE);
    }
    replyLike.restore();
    return replyLike;
  }

  private PersonalArtworkQuestionReplyLikeResult result(
      PersonalArtworkQuestionReplyLike replyLike, boolean liked) {
    return new PersonalArtworkQuestionReplyLikeResult(
        replyLike.getPersonalQuestionReplyId(),
        liked,
        repository.countByPersonalQuestionReplyIdAndDeletedAtIsNull(
            replyLike.getPersonalQuestionReplyId()),
        replyLike.getCreatedAt(),
        replyLike.getDeletedAt());
  }
}
