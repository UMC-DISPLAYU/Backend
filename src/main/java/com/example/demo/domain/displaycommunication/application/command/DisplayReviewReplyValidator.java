package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.error.DisplayCommunicationErrorCode;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewRepository;
import com.example.demo.domain.displaycommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DisplayReviewReplyValidator {

  private final DisplayReviewRepository displayReviewRepository;
  private final UserExistenceRepository userExistenceRepository;

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

  public void validateReplyTarget(DisplayReview displayReview, Long displayId) {
    if (!displayReview.belongsToDisplay(displayId)) {
      throw new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_NOT_FOUND);
    }
  }

  public void validateUserExists(Long userId) {
    if (!userExistenceRepository.existsById(userId)) {
      throw new BusinessException(DisplayCommunicationErrorCode.USER_NOT_FOUND);
    }
  }

  public void validateContent(String content) {
    if (content == null || content.isBlank() || content.length() > 300) {
      throw new BusinessException(
          DisplayCommunicationErrorCode.INVALID_DISPLAY_REVIEW_REPLY_CONTENT);
    }
  }
}
