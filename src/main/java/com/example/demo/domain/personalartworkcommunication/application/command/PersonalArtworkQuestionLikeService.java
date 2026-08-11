package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.permission.PersonalArtworkCommunicationPermissionChecker;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionLikeResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkExistenceRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionLikeRepository.PersonalArtworkQuestionLikeSnapshot;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalArtworkQuestionLikeService {

  private final PersonalArtworkQuestionLikeRepository personalArtworkQuestionLikeRepository;
  private final PersonalArtworkQuestionValidator personalArtworkQuestionValidator;
  private final PersonalArtworkExistenceRepository personalArtworkExistenceRepository;
  private final PersonalArtworkCommunicationPermissionChecker permissionChecker;

  public PersonalArtworkQuestionLikeResult likeQuestion(
      PersonalArtworkQuestionLikeCommand command) {
    validateLikeTarget(command);

    PersonalArtworkQuestionLikeSnapshot snapshot =
        personalArtworkQuestionLikeRepository
            .likeAndGetSnapshot(command.personalQuestionId(), command.userId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_NOT_FOUND));

    return toResult(snapshot);
  }

  public PersonalArtworkQuestionLikeResult cancelQuestionLike(
      PersonalArtworkQuestionLikeCommand command) {
    validateLikeTarget(command);

    PersonalArtworkQuestionLikeSnapshot snapshot =
        personalArtworkQuestionLikeRepository
            .deleteAndGetSnapshot(command.personalQuestionId(), command.userId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_LIKE_NOT_FOUND));

    return toResult(snapshot);
  }

  private void validateLikeTarget(PersonalArtworkQuestionLikeCommand command) {
    personalArtworkQuestionValidator.validatePersonalArtworkExists(command.personalArtworkId());
    personalArtworkQuestionValidator.validateUserExists(command.userId());

    PersonalArtworkQuestion question =
        personalArtworkQuestionValidator.findActiveQuestionForUpdateOrThrow(
            command.personalQuestionId());
    personalArtworkQuestionValidator.validateQuestionTarget(question, command.personalArtworkId());
    boolean isOwner =
        personalArtworkExistenceRepository.existsByIdAndUserId(
            command.personalArtworkId(), command.userId());
    permissionChecker.requirePersonalQuestionAccessible(question, command.userId(), isOwner);
  }

  private PersonalArtworkQuestionLikeResult toResult(PersonalArtworkQuestionLikeSnapshot snapshot) {
    return new PersonalArtworkQuestionLikeResult(
        snapshot.personalQuestionId(),
        snapshot.liked(),
        snapshot.likeCount(),
        snapshot.createdAt(),
        snapshot.deletedAt());
  }
}
