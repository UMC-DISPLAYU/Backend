package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyLikeResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.error.DisplayCommunicationErrorCode;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyLikeRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyLikeRepository.DisplayReviewReplyLikeSnapshot;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DisplayReviewReplyLikeService {

  private final DisplayReviewValidator displayReviewValidator;
  private final DisplayReviewReplyLikeRepository displayReviewReplyLikeRepository;

  public DisplayReviewReplyLikeResult likeReviewReply(DisplayReviewReplyLikeCommand command) {
    validateLikeTarget(command);

    DisplayReviewReplyLikeSnapshot snapshot =
        displayReviewReplyLikeRepository
            .likeAndGetSnapshot(command.displayReviewReplyId(), command.userId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        DisplayCommunicationErrorCode.DISPLAY_REVIEW_REPLY_NOT_FOUND));

    return toResult(snapshot);
  }

  public DisplayReviewReplyLikeResult cancelReviewReplyLike(DisplayReviewReplyLikeCommand command) {
    validateLikeTarget(command);

    DisplayReviewReplyLikeSnapshot snapshot =
        displayReviewReplyLikeRepository
            .deleteAndGetSnapshot(command.displayReviewReplyId(), command.userId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        DisplayCommunicationErrorCode.DISPLAY_REVIEW_REPLY_LIKE_NOT_FOUND));

    return toResult(snapshot);
  }

  private void validateLikeTarget(DisplayReviewReplyLikeCommand command) {
    displayReviewValidator.validateDisplayExists(command.displayId());
    displayReviewValidator.validateUserExists(command.userId());

    DisplayReview displayReview =
        displayReviewValidator.findReviewOrThrow(command.displayReviewId());
    displayReviewValidator.validateReviewTarget(displayReview, command.displayId());

    DisplayReviewReply displayReviewReply =
        displayReviewValidator.findReplyOrThrow(command.displayReviewReplyId());
    displayReviewValidator.validateReplyTarget(displayReviewReply, command.displayReviewId());
  }

  private DisplayReviewReplyLikeResult toResult(DisplayReviewReplyLikeSnapshot snapshot) {
    return new DisplayReviewReplyLikeResult(
        snapshot.displayReviewReplyId(),
        snapshot.liked(),
        Math.toIntExact(snapshot.likeCount()),
        snapshot.createdAt(),
        snapshot.deletedAt());
  }
}
