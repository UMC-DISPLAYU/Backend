package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.displaycommunication.application.result.DisplayReviewLikeResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.error.DisplayCommunicationErrorCode;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewLikeRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewLikeRepository.DisplayReviewLikeSnapshot;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DisplayReviewLikeService {
  private final DisplayReviewValidator validator;
  private final DisplayReviewLikeRepository displayReviewLikeRepository;

  public DisplayReviewLikeResult toggle(DisplayReviewLikeCommand command) {
    validator.validateUserExists(command.userId());
    DisplayReview displayReview = validator.findReviewOrThrow(command.displayReviewId());
    validator.validateReviewTarget(displayReview, command.displayId());

    DisplayReviewLikeSnapshot snapshot =
        displayReviewLikeRepository
            .toggleAndGetSnapshot(command.displayReviewId(), command.userId())
            .orElseThrow(
                () ->
                    new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_NOT_FOUND));

    return new DisplayReviewLikeResult(
        snapshot.displayReviewId(),
        snapshot.liked(),
        Math.toIntExact(snapshot.likeCount()),
        snapshot.createdAt(),
        snapshot.deletedAt());
  }
}
