package com.example.demo.domain.archive.domain.type;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ArchiveDisplayStatusTest {

  private static final LocalDate TODAY = LocalDate.of(2026, 7, 13);

  @Test
  void returnsUpcomingWhenStartDateIsAfterToday() {
    LocalDate startDate = TODAY.plusDays(1);
    LocalDate endDate = TODAY.plusDays(10);

    assertThat(ArchiveDisplayStatus.from(startDate, endDate, TODAY))
        .isEqualTo(ArchiveDisplayStatus.UPCOMING);
  }

  @Test
  void returnsOngoingWhenStartDateIsToday() {
    LocalDate startDate = TODAY;
    LocalDate endDate = TODAY.plusDays(10);

    assertThat(ArchiveDisplayStatus.from(startDate, endDate, TODAY))
        .isEqualTo(ArchiveDisplayStatus.ONGOING);
  }

  @Test
  void returnsEndedWhenEndDateIsBeforeToday() {
    LocalDate startDate = TODAY.minusDays(10);
    LocalDate endDate = TODAY.minusDays(1);

    assertThat(ArchiveDisplayStatus.from(startDate, endDate, TODAY))
        .isEqualTo(ArchiveDisplayStatus.ENDED);
  }

  @Test
  void returnsClosingSoonWhenEndDateIsToday() {
    LocalDate startDate = TODAY.minusDays(10);
    LocalDate endDate = TODAY;

    assertThat(ArchiveDisplayStatus.from(startDate, endDate, TODAY))
        .isEqualTo(ArchiveDisplayStatus.CLOSING_SOON);
  }

  @Test
  void returnsClosingSoonWhenEndDateIsExactlyThreeDaysAway() {
    LocalDate startDate = TODAY.minusDays(10);
    LocalDate endDate = TODAY.plusDays(3);

    assertThat(ArchiveDisplayStatus.from(startDate, endDate, TODAY))
        .isEqualTo(ArchiveDisplayStatus.CLOSING_SOON);
  }

  @Test
  void returnsOngoingWhenEndDateIsFourDaysAway() {
    LocalDate startDate = TODAY.minusDays(10);
    LocalDate endDate = TODAY.plusDays(4);

    assertThat(ArchiveDisplayStatus.from(startDate, endDate, TODAY))
        .isEqualTo(ArchiveDisplayStatus.ONGOING);
  }

  @Test
  void endedTakesPriorityWhenBothStartAndEndAreBeforeToday() {
    LocalDate startDate = TODAY.minusDays(20);
    LocalDate endDate = TODAY.minusDays(10);

    assertThat(ArchiveDisplayStatus.from(startDate, endDate, TODAY))
        .isEqualTo(ArchiveDisplayStatus.ENDED);
  }
}
