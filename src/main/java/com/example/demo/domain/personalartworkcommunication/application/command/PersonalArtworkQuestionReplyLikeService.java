package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.permission.PersonalArtworkCommunicationPermissionChecker;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionReplyLikeResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyLikeRepository.PersonalArtworkQuestionReplyLikeSnapshot;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonalArtworkQuestionReplyLikeService {

  private final PersonalArtworkQuestionReplyLikeRepository repository;
  private final PersonalArtworkQuestionValidator validator;
  private final PersonalArtworkCommunicationPermissionChecker permissionChecker;

  @Transactional
  public PersonalArtworkQuestionReplyLikeResult toggleReplyLike(
      PersonalArtworkQuestionReplyLikeCommand command) {
    validator.validatePersonalArtworkExists(command.personalArtworkId());
    validator.validateUserExists(command.userId());

    PersonalArtworkQuestion question =
        validator.findActiveQuestionForUpdateOrThrow(command.personalQuestionId());
    validator.validateQuestionTarget(question, command.personalArtworkId());
    boolean isOwner =
        permissionChecker.isPersonalArtworkOwner(command.personalArtworkId(), command.userId());
    permissionChecker.requirePersonalQuestionAccessible(question, command.userId(), isOwner);

    PersonalArtworkQuestionReply reply =
        validator.findActiveReplyForUpdateOrThrow(command.personalQuestionReplyId());
    validator.validateReplyBelongsToQuestion(reply, command.personalQuestionId());

    PersonalArtworkQuestionReplyLikeSnapshot snapshot =
        repository
            .toggleAndGetSnapshot(command.personalQuestionReplyId(), command.userId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_REPLY_NOT_FOUND));

    return new PersonalArtworkQuestionReplyLikeResult(
        snapshot.personalQuestionReplyId(),
        snapshot.liked(),
        snapshot.likeCount(),
        snapshot.createdAt(),
        snapshot.deletedAt());
  }
}
