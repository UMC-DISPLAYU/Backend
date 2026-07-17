package com.example.demo.domain.archive.application.result;

import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import java.time.LocalDateTime;

public record ArchiveWorkResult(
    Long archiveWorkId, Long displayArtworkId, Long userId, String memo, LocalDateTime savedAt) {

  public static ArchiveWorkResult from(ArchiveWork archiveWork, String memo) {
    return new ArchiveWorkResult(
        archiveWork.getId(),
        archiveWork.getDisplayArtworkId(),
        archiveWork.getUserId(),
        memo,
        archiveWork.getSavedAt());
  }
}
