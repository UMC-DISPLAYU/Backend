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

  public static ArchiveDisplayResult from(
      ArchiveDisplay archiveDisplay, String memo, DisplaySummaryResult summary, LocalDate today) {
    return new ArchiveDisplayResult(
        summary == null ? null : summary.posterImageUrl(),
        summary == null
            ? null
            : ArchiveDisplayStatus.from(summary.startDate(), summary.endDate(), today),
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
}
