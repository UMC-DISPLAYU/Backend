package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingLikeResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingLikeRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalArtworkFeelingLikeService {

  private final PersonalArtworkFeelingLikeRepository personalArtworkFeelingLikeRepository;
  private final PersonalArtworkFeelingValidator personalArtworkFeelingValidator;

  public PersonalArtworkFeelingLikeResult like(PersonalArtworkFeelingLikeCommand command) {
    validateFeeling(command);

    PersonalArtworkFeelingLike feelingLike =
        personalArtworkFeelingLikeRepository
            .findByPersonalFeelingIdAndUserId(command.personalFeelingId(), command.userId())
            .map(this::restoreDeletedLike)
            .orElseGet(
                () ->
                    PersonalArtworkFeelingLike.create(
                        command.personalFeelingId(), command.userId()));

    try {
      feelingLike = personalArtworkFeelingLikeRepository.save(feelingLike);
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE, exception);
    }

    return result(feelingLike, true);
  }

  public PersonalArtworkFeelingLikeResult cancel(PersonalArtworkFeelingLikeCommand command) {
    validateFeeling(command);

    PersonalArtworkFeelingLike feelingLike =
        personalArtworkFeelingLikeRepository
            .findByPersonalFeelingIdAndUserId(command.personalFeelingId(), command.userId())
            .filter(like -> !like.isDeleted())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

    feelingLike.delete();

    return result(feelingLike, false);
  }

  private void validateFeeling(PersonalArtworkFeelingLikeCommand command) {
    personalArtworkFeelingValidator.validatePersonalArtworkExists(command.personalArtworkId());
    personalArtworkFeelingValidator.validateUserExists(command.userId());

    PersonalArtworkFeeling personalArtworkFeeling =
        personalArtworkFeelingValidator.findFeelingOrThrow(command.personalFeelingId());
    personalArtworkFeelingValidator.validateReplyTarget(
        personalArtworkFeeling, command.personalArtworkId());
  }

  private PersonalArtworkFeelingLike restoreDeletedLike(PersonalArtworkFeelingLike feelingLike) {
    if (!feelingLike.isDeleted()) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE);
    }
    feelingLike.restore();
    return feelingLike;
  }

  private PersonalArtworkFeelingLikeResult result(
      PersonalArtworkFeelingLike feelingLike, boolean liked) {
    return new PersonalArtworkFeelingLikeResult(
        feelingLike.getPersonalFeelingId(),
        liked,
        Math.toIntExact(
            personalArtworkFeelingLikeRepository.countByPersonalFeelingIdAndDeletedAtIsNull(
                feelingLike.getPersonalFeelingId())),
        feelingLike.getCreatedAt(),
        feelingLike.getDeletedAt());
  }
}
