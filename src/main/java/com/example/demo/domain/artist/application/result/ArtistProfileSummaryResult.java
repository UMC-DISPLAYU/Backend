package com.example.demo.domain.artist.application.result;

import com.example.demo.domain.artist.application.query.ArtistProfileSummaryQueryResult;
import com.example.demo.domain.artist.domain.type.ActivityCategory;
import java.util.List;

public record ArtistProfileSummaryResult(
    Long artistProfileId,
    Long userId,
    String artistName,
    String profileImageUrl,
    List<ActivityCategory> fields) {

  public static ArtistProfileSummaryResult from(ArtistProfileSummaryQueryResult queryResult) {
    return new ArtistProfileSummaryResult(
        queryResult.artistProfileId(),
        queryResult.userId(),
        queryResult.artistName(),
        queryResult.profileImageUrl(),
        queryResult.fields());
  }
}
