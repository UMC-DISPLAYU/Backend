package com.example.demo.domain.displaycommunication.application.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.error.DisplayCommunicationErrorCode;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import org.junit.jupiter.api.Test;

class DisplayReviewPermissionCheckerTest {

  private final DisplayReviewPermissionChecker permissionChecker =
      new DisplayReviewPermissionChecker();

  @Test
  void reviewWriterHasPermission() {
    DisplayReview review = DisplayReview.create(1L, 2L, "후기", List.of());

    assertThatCode(() -> permissionChecker.requireReviewWriter(review, 2L))
        .doesNotThrowAnyException();
  }

  @Test
  void nonWriterCannotModifyReview() {
    DisplayReview review = DisplayReview.create(1L, 2L, "후기", List.of());

    assertThatThrownBy(() -> permissionChecker.requireReviewWriter(review, 3L))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayCommunicationErrorCode.DISPLAY_REVIEW_FORBIDDEN));
  }

  @Test
  void replyWriterHasPermission() {
    DisplayReviewReply reply = DisplayReviewReply.create(10L, 3L, "답글", List.of());

    assertThatCode(() -> permissionChecker.requireReplyWriter(reply, 3L))
        .doesNotThrowAnyException();
  }

  @Test
  void nonWriterCannotModifyReply() {
    DisplayReviewReply reply = DisplayReviewReply.create(10L, 3L, "답글", List.of());

    assertThatThrownBy(() -> permissionChecker.requireReplyWriter(reply, 4L))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayCommunicationErrorCode.DISPLAY_REVIEW_REPLY_FORBIDDEN));
  }
}
