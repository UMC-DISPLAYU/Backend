package com.example.demo.domain.displayartwork.application.result;

import com.example.demo.domain.displayartwork.application.query.ArtworkSummaryQueryResult;

public record ArtworkSummaryResult(
    Long displayArtworkId, String artworkName, String artistName, String artworkImageUrl) {

  public static ArtworkSummaryResult from(ArtworkSummaryQueryResult queryResult) {
    return new ArtworkSummaryResult(
        queryResult.displayArtworkId(),
        queryResult.artworkName(),
        queryResult.artistName(),
        queryResult.artworkImageUrl());
  }
}
