package com.example.demo.domain.archive.domain.type;

import java.time.LocalDate;

public enum ArchiveDisplayStatus {
  UPCOMING,
  ONGOING,
  CLOSING_SOON,
  ENDED;

  private static final int CLOSING_SOON_WITHIN_DAYS = 3;

  public static ArchiveDisplayStatus from(LocalDate startDate, LocalDate endDate, LocalDate today) {
    if (endDate.isBefore(today)) {
      return ENDED;
    }
    if (startDate.isAfter(today)) {
      return UPCOMING;
    }
    if (!endDate.isAfter(today.plusDays(CLOSING_SOON_WITHIN_DAYS))) {
      return CLOSING_SOON;
    }
    return ONGOING;
  }
}
