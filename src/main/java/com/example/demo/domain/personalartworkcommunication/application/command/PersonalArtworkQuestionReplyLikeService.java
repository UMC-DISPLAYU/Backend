package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.permission.PersonalArtworkCommunicationPermissionChecker;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionReplyLikeResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkExistenceRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyLikeRepository.PersonalArtworkQuestionReplyLikeSnapshot;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalArtworkQuestionReplyLikeService {

  private final PersonalArtworkQuestionReplyLikeRepository repository;
  private final PersonalArtworkQuestionValidator validator;
  private final PersonalArtworkExistenceRepository personalArtworkExistenceRepository;
  private final PersonalArtworkCommunicationPermissionChecker permissionChecker;

  public PersonalArtworkQuestionReplyLikeResult likeReply(
      PersonalArtworkQuestionReplyLikeCommand command) {
    validateLikeTarget(command);

    PersonalArtworkQuestionReplyLikeSnapshot snapshot =
        repository
            .likeAndGetSnapshot(command.personalQuestionReplyId(), command.userId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_REPLY_NOT_FOUND));

    return toResult(snapshot);
  }

  public PersonalArtworkQuestionReplyLikeResult cancelReplyLike(
      PersonalArtworkQuestionReplyLikeCommand command) {
    validateLikeTarget(command);

    PersonalArtworkQuestionReplyLikeSnapshot snapshot =
        repository
            .deleteAndGetSnapshot(command.personalQuestionReplyId(), command.userId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode
                            .PERSONAL_QUESTION_REPLY_LIKE_NOT_FOUND));

    return toResult(snapshot);
  }

  private void validateLikeTarget(PersonalArtworkQuestionReplyLikeCommand command) {
    validator.validatePersonalArtworkExists(command.personalArtworkId());
    validator.validateUserExists(command.userId());

    PersonalArtworkQuestion question =
        validator.findActiveQuestionForUpdateOrThrow(command.personalQuestionId());
    validator.validateQuestionTarget(question, command.personalArtworkId());
    boolean isOwner =
        personalArtworkExistenceRepository.existsByIdAndUserId(
            command.personalArtworkId(), command.userId());
    permissionChecker.requirePersonalQuestionAccessible(question, command.userId(), isOwner);

    PersonalArtworkQuestionReply reply =
        validator.findActiveReplyForUpdateOrThrow(command.personalQuestionReplyId());
    validator.validateReplyBelongsToQuestion(reply, command.personalQuestionId());
  }

  private PersonalArtworkQuestionReplyLikeResult toResult(
      PersonalArtworkQuestionReplyLikeSnapshot snapshot) {
    return new PersonalArtworkQuestionReplyLikeResult(
        snapshot.personalQuestionReplyId(),
        snapshot.liked(),
        snapshot.likeCount(),
        snapshot.createdAt(),
        snapshot.deletedAt());
  }
}
