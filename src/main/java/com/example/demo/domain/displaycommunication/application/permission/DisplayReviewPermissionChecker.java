package com.example.demo.domain.displaycommunication.application.permission;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.error.DisplayCommunicationErrorCode;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class DisplayReviewPermissionChecker {

  public void requireReviewWriter(DisplayReview displayReview, Long userId) {
    if (!displayReview.isWrittenBy(userId)) {
      throw new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_FORBIDDEN);
    }
  }

  public void requireReplyWriter(DisplayReviewReply displayReviewReply, Long userId) {
    if (!displayReviewReply.isWrittenBy(userId)) {
      throw new BusinessException(DisplayCommunicationErrorCode.DISPLAY_REVIEW_REPLY_FORBIDDEN);
    }
  }
}
