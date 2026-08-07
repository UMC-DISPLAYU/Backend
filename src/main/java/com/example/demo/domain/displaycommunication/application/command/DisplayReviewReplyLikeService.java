package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyLikeResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReplyLike;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyLikeRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DisplayReviewReplyLikeService {

  private final DisplayReviewValidator displayReviewValidator;
  private final DisplayReviewReplyLikeRepository displayReviewReplyLikeRepository;

  public DisplayReviewReplyLikeResult like(DisplayReviewReplyLikeCommand command) {
    displayReviewValidator.validateDisplayExists(command.displayId());
    displayReviewValidator.validateUserExists(command.userId());
    validateReply(command);

    DisplayReviewReplyLike displayReviewReplyLike =
        displayReviewReplyLikeRepository
            .findByDisplayReviewReplyIdAndUserId(command.displayReviewReplyId(), command.userId())
            .map(this::restoreDeletedLike)
            .orElseGet(
                () ->
                    DisplayReviewReplyLike.create(
                        command.displayReviewReplyId(), command.userId()));

    try {
      displayReviewReplyLike = displayReviewReplyLikeRepository.save(displayReviewReplyLike);
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE, exception);
    }

    return result(displayReviewReplyLike, true);
  }

  public DisplayReviewReplyLikeResult cancel(DisplayReviewReplyLikeCommand command) {
    displayReviewValidator.validateDisplayExists(command.displayId());
    displayReviewValidator.validateUserExists(command.userId());
    validateReply(command);

    DisplayReviewReplyLike displayReviewReplyLike =
        displayReviewReplyLikeRepository
            .findByDisplayReviewReplyIdAndUserId(command.displayReviewReplyId(), command.userId())
            .filter(like -> !like.isDeleted())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

    displayReviewReplyLike.delete();

    return result(displayReviewReplyLike, false);
  }

  private void validateReply(DisplayReviewReplyLikeCommand command) {
    DisplayReview displayReview =
        displayReviewValidator.findReviewOrThrow(command.displayReviewId());
    displayReviewValidator.validateReviewTarget(displayReview, command.displayId());

    DisplayReviewReply displayReviewReply =
        displayReviewValidator.findReplyOrThrow(command.displayReviewReplyId());
    displayReviewValidator.validateReplyTarget(displayReviewReply, command.displayReviewId());
  }

  private DisplayReviewReplyLike restoreDeletedLike(DisplayReviewReplyLike displayReviewReplyLike) {
    if (!displayReviewReplyLike.isDeleted()) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE);
    }
    displayReviewReplyLike.restore();
    return displayReviewReplyLike;
  }

  private DisplayReviewReplyLikeResult result(
      DisplayReviewReplyLike displayReviewReplyLike, boolean liked) {
    return new DisplayReviewReplyLikeResult(
        displayReviewReplyLike.getDisplayReviewReplyId(),
        liked,
        Math.toIntExact(
            displayReviewReplyLikeRepository.countByDisplayReviewReplyIdAndDeletedAtIsNull(
                displayReviewReplyLike.getDisplayReviewReplyId())),
        displayReviewReplyLike.getCreatedAt(),
        displayReviewReplyLike.getDeletedAt());
  }
}
