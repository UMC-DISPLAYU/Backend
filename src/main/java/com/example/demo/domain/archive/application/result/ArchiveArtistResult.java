package com.example.demo.domain.archive.application.result;

import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import com.example.demo.domain.artist.application.result.ArtistProfileSummaryResult;
import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import com.example.demo.domain.displayartwork.application.result.ArtistWorkStatsResult;
import java.time.LocalDateTime;
import java.util.List;

public record ArchiveArtistResult(
    String profileImageUrl,
    String artistName,
    List<ActivityCategory> fields,
    Long artworkCount,
    Long exhibitionCount,
    Long archiveArtistId,
    Long artistProfileId,
    Long userId,
    LocalDateTime savedAt) {

  public static ArchiveArtistResult from(
      ArchiveArtist archiveArtist,
      ArtistProfileSummaryResult profile,
      ArtistWorkStatsResult stats) {
    return new ArchiveArtistResult(
        profile == null ? null : profile.profileImageUrl(),
        profile == null ? null : profile.artistName(),
        profile == null ? List.of() : profile.fields(),
        stats == null ? null : stats.artworkCount(),
        stats == null ? null : stats.exhibitionCount(),
        archiveArtist.getId(),
        archiveArtist.getArtistProfileId(),
        archiveArtist.getUserId(),
        archiveArtist.getSavedAt());
  }
}
