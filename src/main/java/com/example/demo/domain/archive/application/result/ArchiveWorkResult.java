package com.example.demo.domain.archive.application.result;

import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.displayartwork.application.result.ArtworkSummaryResult;
import java.time.LocalDateTime;

public record ArchiveWorkResult(
    String artworkImageUrl,
    String artworkName,
    String artistName,
    String memo,
    Long archiveWorkId,
    Long displayArtworkId,
    Long userId,
    LocalDateTime savedAt) {

  public static ArchiveWorkResult from(
      ArchiveWork archiveWork, String memo, ArtworkSummaryResult summary) {
    return new ArchiveWorkResult(
        summary == null ? null : summary.artworkImageUrl(),
        summary == null ? null : summary.artworkName(),
        summary == null ? null : summary.artistName(),
        memo,
        archiveWork.getId(),
        archiveWork.getDisplayArtworkId(),
        archiveWork.getUserId(),
        archiveWork.getSavedAt());
  }
}
