package com.example.demo.domain.archive.application.result;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import java.time.LocalDateTime;

public record ArchiveDisplayResult(
    Long archiveDisplayId, Long displayId, Long userId, LocalDateTime savedAt) {

  public static ArchiveDisplayResult from(ArchiveDisplay archiveDisplay) {
    return new ArchiveDisplayResult(
        archiveDisplay.getId(),
        archiveDisplay.getDisplayId(),
        archiveDisplay.getUserId(),
        archiveDisplay.getSavedAt());
  }
}
