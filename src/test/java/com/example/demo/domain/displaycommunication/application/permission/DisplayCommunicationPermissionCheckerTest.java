package com.example.demo.domain.displaycommunication.application.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.error.DisplayCommunicationErrorCode;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import org.junit.jupiter.api.Test;

class DisplayCommunicationPermissionCheckerTest {

  private final DisplayCommunicationPermissionChecker permissionChecker =
      new DisplayCommunicationPermissionChecker(mock(DisplayReviewAccessRepository.class));

  @Test
  void reviewWriterIsAllowed() {
    DisplayReview review = DisplayReview.create(1L, 2L, "review", List.of());

    assertThatCode(() -> permissionChecker.requireReviewWriter(review, 2L))
        .doesNotThrowAnyException();
  }

  @Test
  void nonReviewWriterIsDenied() {
    DisplayReview review = DisplayReview.create(1L, 2L, "review", List.of());

    assertError(
        () -> permissionChecker.requireReviewWriter(review, 3L),
        DisplayCommunicationErrorCode.DISPLAY_REVIEW_FORBIDDEN);
  }

  @Test
  void nonReplyWriterIsDenied() {
    DisplayReviewReply reply = DisplayReviewReply.create(1L, 2L, "reply", List.of());

    assertError(
        () -> permissionChecker.requireReviewReplyWriter(reply, 3L),
        DisplayCommunicationErrorCode.DISPLAY_REVIEW_REPLY_FORBIDDEN);
  }

  private void assertError(Runnable action, DisplayCommunicationErrorCode errorCode) {
    assertThatThrownBy(action::run)
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(errorCode));
  }
}
