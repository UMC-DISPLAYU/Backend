package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview.ImageInfo;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.error.DisplayCommunicationErrorCode;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayExistenceRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository.DisplayReviewAccess;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyRepository;
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
  private final DisplayReviewReplyRepository displayReviewReplyRepository;
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

  public DisplayReviewAccess findDisplayAccessOrThrow(Long displayId) {
    return displayReviewAccessRepository
        .findByDisplayId(displayId)
        .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));
  }

  public void validateDisplayIsWritable(DisplayReviewAccess access) {
    LocalDate today = LocalDate.now(clock);
    boolean isStarted = !today.isBefore(access.startDate());
    if (!access.published() || !isStarted) {
      throw new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_NOT_WRITABLE);
    }
  }

  public void validateUserExists(Long userId) {
    if (!userExistenceRepository.existsById(userId)) {
      throw new BusinessException(DisplayCommunicationErrorCode.USER_NOT_FOUND);
    }
  }

  public DisplayReview findReviewOrThrow(Long displayReviewId) {
    DisplayReview displayReview = findReviewIncludingDeletedOrThrow(displayReviewId);
    if (displayReview.isDeleted()) {
      throw new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_NOT_FOUND);
    }
    return displayReview;
  }

  public DisplayReview findReviewIncludingDeletedOrThrow(Long displayReviewId) {
    return displayReviewRepository
        .findById(displayReviewId)
        .orElseThrow(
            () -> new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_NOT_FOUND));
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

  public DisplayReviewReply findReplyOrThrow(Long displayReviewReplyId) {
    DisplayReviewReply displayReviewReply =
        displayReviewReplyRepository
            .findById(displayReviewReplyId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        DisplayCommunicationErrorCode.DISPLAY_REVIEW_REPLY_NOT_FOUND));
    if (displayReviewReply.isDeleted()) {
      throw new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_REPLY_NOT_FOUND);
    }
    return displayReviewReply;
  }

  public void validateAccessibleReply(
      DisplayReviewReply displayReviewReply, Long displayReviewId, Long userId) {
    validateReplyTarget(displayReviewReply, displayReviewId);
    validateReplyWriter(displayReviewReply, userId);
  }

  public void validateReplyTarget(DisplayReviewReply displayReviewReply, Long displayReviewId) {
    if (!displayReviewReply.belongsToReview(displayReviewId)) {
      throw new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_REPLY_NOT_FOUND);
    }
  }

  private void validateReplyWriter(DisplayReviewReply displayReviewReply, Long userId) {
    if (!displayReviewReply.isWrittenBy(userId)) {
      throw new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_REPLY_FORBIDDEN);
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

  public void validateReplyImages(List<DisplayReviewReply.ImageInfo> images) {
    if (images == null || images.size() > 5) {
      throw new BusinessException(
          DisplayCommunicationErrorCode.INVALID_DISPLAY_REVIEW_REPLY_IMAGES);
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
      throw new BusinessException(
          DisplayCommunicationErrorCode.INVALID_DISPLAY_REVIEW_REPLY_IMAGES);
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
