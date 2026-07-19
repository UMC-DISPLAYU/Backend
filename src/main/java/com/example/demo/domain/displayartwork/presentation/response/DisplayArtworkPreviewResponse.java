package com.example.demo.domain.displayartwork.presentation.response;

import java.util.List;

public record DisplayArtworkPreviewResponse(
    List<ArtworkCardResponse> artworks, int page, int size, boolean isLast) {

  public record ArtworkCardResponse(
      Long artworkId,
      String artworkName,
      String artworkImageUrl,
      int imageWidth,
      int imageHeight,
      ExhibitionInfoResponse exhibitionInfo) {}

  public record ExhibitionInfoResponse(
      Long displayId, String exhibitionTitle, String exhibitionPeriod, String exhibitionLocation) {}
}
