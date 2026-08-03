package com.example.demo.domain.displayartwork.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record DisplayArtworkByArtistResponse(List<ArtworkCardResponse> artworks) {

  public record ArtworkCardResponse(
      Long artworkId,
      String artworkName,
      String artistName,
      String artworkImageUrl,
      int imageWidth,
      int imageHeight,
      LocalDateTime createdAt,
      ExhibitionInfoResponse exhibitionInfo) {}

  public record ExhibitionInfoResponse(
      Long displayId, String exhibitionTitle, String exhibitionPeriod, String exhibitionLocation) {}
}
