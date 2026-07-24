package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview.ImageInfo;
import com.example.demo.domain.displaycommunication.domain.error.DisplayCommunicationErrorCode;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayExistenceRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository.DisplayReviewAccess;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewRepository;
import com.example.demo.domain.displaycommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DisplayReviewValidator {

  private final DisplayExistenceRepository displayExistenceRepository;
  private final DisplayReviewAccessRepository displayReviewAccessRepository;
  private final DisplayReviewRepository displayReviewRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final Clock clock;

  public void validateDisplayExists(Long displayId) {
    if (!displayExistenceRepository.existsById(displayId)) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND);
    }
  }

  public DisplayReviewAccess findDisplayAccessOrThrow(Long displayId, Long userId) {
    return displayReviewAccessRepository
        .findByDisplayIdAndUserId(displayId, userId)
        .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));
  }

  public void validateDisplayIsOngoing(DisplayReviewAccess access) {
    LocalDate today = LocalDate.now(clock);
    boolean isOngoing = !today.isBefore(access.startDate()) && !today.isAfter(access.endDate());
    if (!access.published() || !isOngoing) {
      throw new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_NOT_WRITABLE);
    }
  }

  public void validateUserExists(Long userId) {
    if (!userExistenceRepository.existsById(userId)) {
      throw new BusinessException(DisplayCommunicationErrorCode.USER_NOT_FOUND);
    }
  }

  public DisplayReview findReviewOrThrow(Long displayReviewId) {
    DisplayReview displayReview =
        displayReviewRepository
            .findById(displayReviewId)
            .orElseThrow(
                () ->
                    new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_NOT_FOUND));
    if (displayReview.isDeleted()) {
      throw new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_NOT_FOUND);
    }
    return displayReview;
  }

  public void validateReviewTarget(DisplayReview displayReview, Long displayId) {
    if (!displayReview.belongsToDisplay(displayId)) {
      throw new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_NOT_FOUND);
    }
  }

  public void validateWriter(DisplayReview displayReview, Long userId) {
    if (!displayReview.isWrittenBy(userId)) {
      throw new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_FORBIDDEN);
    }
  }

  public void validateAccessibleReview(DisplayReview displayReview, Long displayId, Long userId) {
    validateReviewTarget(displayReview, displayId);
    validateWriter(displayReview, userId);
  }

  public void validateReviewNotExists(Long displayId, Long userId) {
    if (displayReviewRepository.existsByDisplayIdAndUserId(displayId, userId)) {
      throw new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_ALREADY_EXISTS);
    }
  }

  public void validateContent(String content) {
    if (content == null || content.isBlank() || content.length() > 300) {
      throw new BusinessException(DisplayCommunicationErrorCode.INVALID_DISPLAY_REVIEW_CONTENT);
    }
  }

  public void validateReplyContent(String content) {
    if (content == null || content.isBlank() || content.length() > 300) {
      throw new BusinessException(
          DisplayCommunicationErrorCode.INVALID_DISPLAY_REVIEW_REPLY_CONTENT);
    }
  }

  public void validateImages(List<ImageInfo> images) {
    if (images == null || images.size() > 5) {
      throw new BusinessException(DisplayCommunicationErrorCode.INVALID_DISPLAY_REVIEW_IMAGES);
    }
    if (images.stream()
        .anyMatch(
            image ->
                image == null
                    || image.imageUrl() == null
                    || image.imageUrl().isBlank()
                    || image.imageUrl().length() > 2048
                    || image.width() <= 0
                    || image.height() <= 0)) {
      throw new BusinessException(DisplayCommunicationErrorCode.INVALID_DISPLAY_REVIEW_IMAGES);
    }
  }
}
