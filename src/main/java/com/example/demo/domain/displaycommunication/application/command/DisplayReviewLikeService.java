package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.displaycommunication.application.result.DisplayReviewLikeResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewLike;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewLikeRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DisplayReviewLikeService {
  private final DisplayReviewValidator displayReviewValidator;
  private final DisplayReviewLikeRepository displayReviewLikeRepository;

  public DisplayReviewLikeResult like(DisplayReviewLikeCommand command) {
    displayReviewValidator.validateDisplayExists(command.displayId());
    displayReviewValidator.validateUserExists(command.userId());
    validateReview(command);

    DisplayReviewLike displayReviewLike =
        displayReviewLikeRepository
            .findByDisplayReviewIdAndUserId(command.displayReviewId(), command.userId())
            .map(this::restoreDeletedLike)
            .orElseGet(() -> DisplayReviewLike.create(command.displayReviewId(), command.userId()));

    try {
      displayReviewLike = displayReviewLikeRepository.save(displayReviewLike);
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE, exception);
    }

    return result(displayReviewLike, true);
  }

  public DisplayReviewLikeResult cancel(DisplayReviewLikeCommand command) {
    displayReviewValidator.validateDisplayExists(command.displayId());
    displayReviewValidator.validateUserExists(command.userId());
    validateReview(command);

    DisplayReviewLike displayReviewLike =
        displayReviewLikeRepository
            .findByDisplayReviewIdAndUserId(command.displayReviewId(), command.userId())
            .filter(like -> !like.isDeleted())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));

    displayReviewLike.delete();

    return result(displayReviewLike, false);
  }

  private void validateReview(DisplayReviewLikeCommand command) {
    DisplayReview displayReview =
        displayReviewValidator.findReviewOrThrow(command.displayReviewId());
    displayReviewValidator.validateReviewTarget(displayReview, command.displayId());
  }

  private DisplayReviewLike restoreDeletedLike(DisplayReviewLike displayReviewLike) {
    if (!displayReviewLike.isDeleted()) {
      throw new BusinessException(GlobalErrorCode.DUPLICATE_RESOURCE);
    }
    displayReviewLike.restore();
    return displayReviewLike;
  }

  private DisplayReviewLikeResult result(DisplayReviewLike displayReviewLike, boolean liked) {
    return new DisplayReviewLikeResult(
        displayReviewLike.getDisplayReviewId(),
        liked,
        Math.toIntExact(
            displayReviewLikeRepository.countByDisplayReviewIdAndDeletedAtIsNull(
                displayReviewLike.getDisplayReviewId())),
        displayReviewLike.getCreatedAt(),
        displayReviewLike.getDeletedAt());
  }
}
