package com.example.demo.domain.display.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.domain.type.DisplayPeriodStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class DisplayPeriodTest {

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
