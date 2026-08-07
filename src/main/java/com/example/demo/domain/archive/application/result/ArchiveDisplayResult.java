package com.example.demo.domain.archive.application.result;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.type.ArchiveDisplayStatus;
import com.example.demo.domain.display.application.result.DisplaySummaryResult;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ArchiveDisplayResult(
    String posterImageUrl,
    ArchiveDisplayStatus status,
    String title,
    String organization,
    String department,
    LocalDate startedAt,
    LocalDate endedAt,
    String location,
    String memo,
    Long archiveDisplayId,
    Long displayId,
    Long userId,
    LocalDateTime savedAt) {

  private static final int CLOSING_SOON_WITHIN_DAYS = 3;

  public static ArchiveDisplayResult from(
      ArchiveDisplay archiveDisplay, String memo, DisplaySummaryResult summary, LocalDate today) {
    return new ArchiveDisplayResult(
        summary == null ? null : summary.posterImageUrl(),
        summary == null ? null : computeStatus(summary.startDate(), summary.endDate(), today),
        summary == null ? null : summary.title(),
        summary == null ? null : summary.organization(),
        summary == null ? null : summary.department(),
        summary == null ? null : summary.startDate(),
        summary == null ? null : summary.endDate(),
        summary == null ? null : summary.placeName(),
        memo,
        archiveDisplay.getId(),
        archiveDisplay.getDisplayId(),
        archiveDisplay.getUserId(),
        archiveDisplay.getSavedAt());
  }

  private static ArchiveDisplayStatus computeStatus(
      LocalDate startDate, LocalDate endDate, LocalDate today) {
    if (endDate.isBefore(today)) {
      return ArchiveDisplayStatus.ENDED;
    }
    if (startDate.isAfter(today)) {
      return ArchiveDisplayStatus.UPCOMING;
    }
    if (!endDate.isAfter(today.plusDays(CLOSING_SOON_WITHIN_DAYS))) {
      return ArchiveDisplayStatus.CLOSING_SOON;
    }
    return ArchiveDisplayStatus.ONGOING;
  }
}
