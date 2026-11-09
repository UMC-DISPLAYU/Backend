package com.example.demo.domain.displayartwork.presentation.response;

import java.util.List;

public record DisplayArtworkPreviewResponse(
    List<ArtworkCardResponse> artworks, int page, int size, boolean isLast) {

  public record ArtworkCardResponse(
      Long artworkId,
      String artworkName,
      String artistName,
      Long artistUserId,
      String artworkImageUrl,
      int imageWidth,
      int imageHeight,
      ExhibitionInfoResponse exhibitionInfo,
      List<ArtistResponse> artists) {}

  public record ArtistResponse(Long userId, String name) {}

  public record ExhibitionInfoResponse(
      Long displayId, String exhibitionTitle, String exhibitionPeriod, String exhibitionLocation) {}
}
