package com.example.demo.domain.archive.application.result;

import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import java.time.LocalDateTime;

public record ArchiveArtistResult(
    Long archiveArtistId, Long creatorId, Long userId, LocalDateTime savedAt) {

  public static ArchiveArtistResult from(ArchiveArtist archiveArtist) {
    return new ArchiveArtistResult(
        archiveArtist.getId(),
        archiveArtist.getCreatorId(),
        archiveArtist.getUserId(),
        archiveArtist.getSavedAt());
  }
}
