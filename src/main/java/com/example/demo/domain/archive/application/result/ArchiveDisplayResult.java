package com.example.demo.domain.archive.application.result;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import java.time.LocalDateTime;

public record ArchiveDisplayResult(
    Long archiveDisplayId, Long displayId, Long userId, String memo, LocalDateTime savedAt) {

  public static ArchiveDisplayResult from(ArchiveDisplay archiveDisplay, String memo) {
    return new ArchiveDisplayResult(
        archiveDisplay.getId(),
        archiveDisplay.getDisplayId(),
        archiveDisplay.getUserId(),
        memo,
        archiveDisplay.getSavedAt());
  }
}
