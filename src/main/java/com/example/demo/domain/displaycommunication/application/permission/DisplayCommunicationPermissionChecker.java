package com.example.demo.domain.displaycommunication.application.permission;

import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.error.DisplayCommunicationErrorCode;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository.DisplayReviewAccess;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class DisplayCommunicationPermissionChecker {

  private final DisplayReviewAccessRepository displayReviewAccessRepository;

  public DisplayCommunicationPermissionChecker(
      DisplayReviewAccessRepository displayReviewAccessRepository) {
    this.displayReviewAccessRepository = displayReviewAccessRepository;
  }

  public DisplayReviewAccess requireDisplayAccess(Long displayId, Long userId) {
    return displayReviewAccessRepository
        .findByDisplayIdAndUserId(displayId, userId)
        .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));
  }

  public void requireReviewWriter(DisplayReview displayReview, Long userId) {
    if (!displayReview.isWrittenBy(userId)) {
      throw new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_FORBIDDEN);
    }
  }

  public void requireReviewReplyWriter(DisplayReviewReply displayReviewReply, Long userId) {
    if (!displayReviewReply.isWrittenBy(userId)) {
      throw new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_REPLY_FORBIDDEN);
    }
  }
}
