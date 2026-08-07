package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyLikeResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReplyLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyLikeRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalArtworkFeelingReplyLikeService {

  private final PersonalArtworkFeelingReplyLikeRepository personalArtworkFeelingReplyLikeRepository;
  private final PersonalArtworkFeelingValidator personalArtworkFeelingValidator;

  public PersonalArtworkFeelingReplyLikeResult like(
      PersonalArtworkFeelingReplyLikeCommand command) {
    validateReply(command);

    PersonalArtworkFeelingReplyLike replyLike =
        personalArtworkFeelingReplyLikeRepository
            .findByPersonalFeelingReplyIdAndUserId(
                command.personalFeelingReplyId(), command.userId())
            .map(this::restoreDeletedLike)
            .orElseGet(
                () ->
                    PersonalArtworkFeelingReplyLike.create(
                        command.personalFeelingReplyId(), command.userId()));

    try {
      replyLike = personalArtworkFeelingReplyLikeRepository.save(replyLike);
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE, exception);
    }

    return result(replyLike, true);
  }

  public PersonalArtworkFeelingReplyLikeResult cancel(
      PersonalArtworkFeelingReplyLikeCommand command) {
    validateReply(command);

    PersonalArtworkFeelingReplyLike replyLike =
        personalArtworkFeelingReplyLikeRepository
            .findByPersonalFeelingReplyIdAndUserId(
                command.personalFeelingReplyId(), command.userId())
            .filter(like -> !like.isDeleted())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

    replyLike.delete();

    return result(replyLike, false);
  }

  private void validateReply(PersonalArtworkFeelingReplyLikeCommand command) {
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

  private PersonalArtworkFeelingReplyLike restoreDeletedLike(
      PersonalArtworkFeelingReplyLike replyLike) {
    if (!replyLike.isDeleted()) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE);
    }
    replyLike.restore();
    return replyLike;
  }

  private PersonalArtworkFeelingReplyLikeResult result(
      PersonalArtworkFeelingReplyLike replyLike, boolean liked) {
    return new PersonalArtworkFeelingReplyLikeResult(
        replyLike.getPersonalFeelingReplyId(),
        liked,
        personalArtworkFeelingReplyLikeRepository.countByPersonalFeelingReplyIdAndDeletedAtIsNull(
            replyLike.getPersonalFeelingReplyId()),
        replyLike.getCreatedAt(),
        replyLike.getDeletedAt());
  }
}
