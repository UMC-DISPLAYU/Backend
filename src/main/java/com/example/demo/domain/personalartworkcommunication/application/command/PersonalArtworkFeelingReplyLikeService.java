package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyLikeResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyLikeRepository.PersonalArtworkFeelingReplyLikeSnapshot;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonalArtworkFeelingReplyLikeService {

  private final PersonalArtworkFeelingReplyLikeRepository personalArtworkFeelingReplyLikeRepository;
  private final PersonalArtworkFeelingValidator personalArtworkFeelingValidator;

  @Transactional
  public PersonalArtworkFeelingReplyLikeResult likeReply(
      PersonalArtworkFeelingReplyLikeCommand command) {
    validateLikeTarget(command);

    PersonalArtworkFeelingReplyLikeSnapshot snapshot =
        personalArtworkFeelingReplyLikeRepository
            .likeAndGetSnapshot(command.personalFeelingReplyId(), command.userId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_FEELING_REPLY_NOT_FOUND));

    return toResult(snapshot);
  }

  @Transactional
  public PersonalArtworkFeelingReplyLikeResult cancelReplyLike(
      PersonalArtworkFeelingReplyLikeCommand command) {
    validateLikeTarget(command);

    PersonalArtworkFeelingReplyLikeSnapshot snapshot =
        personalArtworkFeelingReplyLikeRepository
            .deleteAndGetSnapshot(command.personalFeelingReplyId(), command.userId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode
                            .PERSONAL_FEELING_REPLY_LIKE_NOT_FOUND));

    return toResult(snapshot);
  }

  private void validateLikeTarget(PersonalArtworkFeelingReplyLikeCommand command) {
    personalArtworkFeelingValidator.validatePersonalArtworkExists(command.personalArtworkId());
    personalArtworkFeelingValidator.validateUserExists(command.userId());

    PersonalArtworkFeeling feeling =
        personalArtworkFeelingValidator.findFeelingOrThrow(command.personalFeelingId());
    personalArtworkFeelingValidator.validateReplyTarget(feeling, command.personalArtworkId());

    PersonalArtworkFeelingReply reply =
        personalArtworkFeelingValidator.findActiveReplyForUpdateOrThrow(
            command.personalFeelingReplyId());
    personalArtworkFeelingValidator.validateReplyTarget(reply, command.personalFeelingId());
  }

  private PersonalArtworkFeelingReplyLikeResult toResult(
      PersonalArtworkFeelingReplyLikeSnapshot snapshot) {
    return new PersonalArtworkFeelingReplyLikeResult(
        snapshot.personalFeelingReplyId(),
        snapshot.liked(),
        snapshot.likeCount(),
        snapshot.createdAt(),
        snapshot.deletedAt());
  }
}
