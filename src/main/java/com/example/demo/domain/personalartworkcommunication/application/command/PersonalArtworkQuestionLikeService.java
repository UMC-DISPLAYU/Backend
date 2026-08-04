package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionLikeResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
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

  public PersonalArtworkQuestionLikeResult toggleQuestionLike(
      PersonalArtworkQuestionLikeCommand command) {
    personalArtworkQuestionValidator.validatePersonalArtworkExists(command.personalArtworkId());
    personalArtworkQuestionValidator.validateUserExists(command.userId());

    PersonalArtworkQuestion question =
        personalArtworkQuestionValidator.findActiveQuestionForUpdateOrThrow(
            command.personalQuestionId());
    personalArtworkQuestionValidator.validateReplyTarget(question, command.personalArtworkId());
    personalArtworkQuestionValidator.validateLikePermission(
        question, command.personalArtworkId(), command.userId());

    PersonalArtworkQuestionLikeSnapshot snapshot =
        personalArtworkQuestionLikeRepository
            .toggleAndGetSnapshot(command.personalQuestionId(), command.userId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_NOT_FOUND));

    return new PersonalArtworkQuestionLikeResult(
        snapshot.personalQuestionId(),
        snapshot.liked(),
        Math.toIntExact(snapshot.likeCount()),
        snapshot.createdAt(),
        snapshot.deletedAt());
  }
}
