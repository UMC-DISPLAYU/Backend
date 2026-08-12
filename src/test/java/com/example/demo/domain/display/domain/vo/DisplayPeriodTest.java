package com.example.demo.domain.display.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.type.DisplayPeriodStatus;
import com.example.demo.global.error.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class DisplayPeriodTest {

  @Test
  void constructorRejectsEndTimeBeforeStartTimeOnSameDate() {
    assertThatThrownBy(
            () ->
                new DisplayPeriod(
                    LocalDate.of(2026, 8, 12),
                    LocalDate.of(2026, 8, 12),
                    LocalTime.of(18, 0),
                    LocalTime.of(9, 0)))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayErrorCode.INVALID_DISPLAY_PERIOD));
  }

  @Test
  void statusAtReturnsUpcomingBeforeStartTimeOnStartDate() {
    DisplayPeriod period =
        new DisplayPeriod(
            LocalDate.of(2026, 8, 12),
            LocalDate.of(2026, 8, 20),
            LocalTime.of(9, 0),
            LocalTime.of(18, 0));

    DisplayPeriodStatus status = period.statusAt(LocalDateTime.of(2026, 8, 12, 1, 0));

    assertThat(status).isEqualTo(DisplayPeriodStatus.UPCOMING);
  }

  @Test
  void statusAtReturnsDisplayingFromStartTimeThroughEndTime() {
    DisplayPeriod period =
        new DisplayPeriod(
            LocalDate.of(2026, 8, 12),
            LocalDate.of(2026, 8, 20),
            LocalTime.of(9, 0),
            LocalTime.of(18, 0));

    assertThat(period.statusAt(LocalDateTime.of(2026, 8, 12, 9, 0)))
        .isEqualTo(DisplayPeriodStatus.DISPLAYING);
    assertThat(period.statusAt(LocalDateTime.of(2026, 8, 20, 18, 0)))
        .isEqualTo(DisplayPeriodStatus.DISPLAYING);
  }

  @Test
  void statusAtReturnsEndedAfterEndTime() {
    DisplayPeriod period =
        new DisplayPeriod(
            LocalDate.of(2026, 8, 12),
            LocalDate.of(2026, 8, 20),
            LocalTime.of(9, 0),
            LocalTime.of(18, 0));

    DisplayPeriodStatus status = period.statusAt(LocalDateTime.of(2026, 8, 20, 18, 1));

    assertThat(status).isEqualTo(DisplayPeriodStatus.ENDED);
  }
}
