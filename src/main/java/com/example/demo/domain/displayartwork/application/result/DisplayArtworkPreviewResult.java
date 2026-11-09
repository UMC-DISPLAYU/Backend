package com.example.demo.domain.displayartwork.application.result;

import java.util.List;

public record DisplayArtworkPreviewResult(
    List<ArtworkCardResult> artworks, int page, int size, boolean isLast) {

  public record ArtworkCardResult(
      Long artworkId,
      String artworkName,
      String artistName,
      Long artistUserId,
      String artworkImageUrl,
      int imageWidth,
      int imageHeight,
      ExhibitionInfoResult exhibitionInfo,
      List<ArtistResult> artists) {}

  public record ArtistResult(Long userId, String name) {}

  public record ExhibitionInfoResult(
      Long displayId, String exhibitionTitle, String exhibitionPeriod, String exhibitionLocation) {}
}
