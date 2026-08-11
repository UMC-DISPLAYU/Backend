package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingLikeResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingLikeRepository.PersonalArtworkFeelingLikeSnapshot;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonalArtworkFeelingLikeService {

  private final PersonalArtworkFeelingLikeRepository personalArtworkFeelingLikeRepository;
  private final PersonalArtworkFeelingValidator personalArtworkFeelingValidator;

  @Transactional
  public PersonalArtworkFeelingLikeResult toggleFeelingLike(
      PersonalArtworkFeelingLikeCommand command) {
    personalArtworkFeelingValidator.validatePersonalArtworkExists(command.personalArtworkId());
    personalArtworkFeelingValidator.validateUserExists(command.userId());

    PersonalArtworkFeeling personalArtworkFeeling =
        personalArtworkFeelingValidator.findFeelingOrThrow(command.personalFeelingId());
    personalArtworkFeelingValidator.validateReplyTarget(
        personalArtworkFeeling, command.personalArtworkId());

    PersonalArtworkFeelingLikeSnapshot snapshot =
        personalArtworkFeelingLikeRepository
            .toggleAndGetSnapshot(command.personalFeelingId(), command.userId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_FEELING_NOT_FOUND));

    return new PersonalArtworkFeelingLikeResult(
        snapshot.personalFeelingId(),
        snapshot.liked(),
        Math.toIntExact(snapshot.likeCount()),
        snapshot.createdAt(),
        snapshot.deletedAt());
  }
}
