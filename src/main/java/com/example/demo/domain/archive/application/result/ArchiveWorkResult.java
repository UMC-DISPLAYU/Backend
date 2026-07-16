package com.example.demo.domain.archive.application.result;

import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import java.time.LocalDateTime;

public record ArchiveWorkResult(
    Long archiveWorkId, Long displayArtworkId, Long userId, LocalDateTime savedAt) {

  public static ArchiveWorkResult from(ArchiveWork archiveWork) {
    return new ArchiveWorkResult(
        archiveWork.getId(),
        archiveWork.getDisplayArtworkId(),
        archiveWork.getUserId(),
        archiveWork.getSavedAt());
  }
}
