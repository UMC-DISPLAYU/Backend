package com.example.demo.domain.displayartwork.application.result;

import com.example.demo.domain.displayartwork.application.query.ArtistWorkStatsQueryResult;

public record ArtistWorkStatsResult(Long userId, Long artworkCount, Long exhibitionCount) {

  public static ArtistWorkStatsResult from(ArtistWorkStatsQueryResult queryResult) {
    return new ArtistWorkStatsResult(
        queryResult.userId(), queryResult.artworkCount(), queryResult.exhibitionCount());
  }
}
