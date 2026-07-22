package com.example.demo.domain.archive.application.result;

import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import java.time.LocalDateTime;

public record ArchiveArtistResult(
    Long archiveArtistId, Long artistProfileId, Long userId, LocalDateTime savedAt) {

  public static ArchiveArtistResult from(ArchiveArtist archiveArtist) {
    return new ArchiveArtistResult(
        archiveArtist.getId(),
        archiveArtist.getArtistProfileId(),
        archiveArtist.getUserId(),
        archiveArtist.getSavedAt());
  }
}
