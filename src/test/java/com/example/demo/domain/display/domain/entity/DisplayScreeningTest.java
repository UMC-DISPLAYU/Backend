package com.example.demo.domain.display.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.type.DisplayScreeningStatus;
import com.example.demo.global.error.BusinessException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DisplayScreeningTest {

  private static final LocalDateTime REQUESTED_AT = LocalDateTime.of(2026, 9, 20, 1, 0);
  private static final LocalDateTime PROCESSED_AT = LocalDateTime.of(2026, 9, 20, 2, 0);

  @Test
  void approveRecordsReviewerAndProcessedTime() {
    DisplayScreening screening = DisplayScreening.request(mock(Display.class), 1L, REQUESTED_AT);

    screening.approve(7L, PROCESSED_AT);

    assertThat(screening.getStatus()).isEqualTo(DisplayScreeningStatus.PUBLISHED);
    assertThat(screening.getReviewerId()).isEqualTo(7L);
    assertThat(screening.getProcessedAt()).isEqualTo(PROCESSED_AT);
    assertThat(screening.getRejectionReason()).isNull();
  }

  @Test
  void rejectRecordsReason() {
    DisplayScreening screening = DisplayScreening.request(mock(Display.class), 1L, REQUESTED_AT);

    screening.reject(7L, "일정 수정", PROCESSED_AT);

    assertThat(screening.getStatus()).isEqualTo(DisplayScreeningStatus.REJECTED);
    assertThat(screening.getRejectionReason()).isEqualTo("일정 수정");
  }

  @Test
  void processedScreeningCannotBeProcessedAgain() {
    DisplayScreening screening = DisplayScreening.request(mock(Display.class), 1L, REQUESTED_AT);
    screening.approve(7L, PROCESSED_AT);

    assertThatThrownBy(() -> screening.reject(7L, "재처리", PROCESSED_AT.plusMinutes(1)))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayErrorCode.INVALID_DISPLAY_REVIEW_STATUS));
  }
}
