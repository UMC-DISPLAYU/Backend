package com.example.demo.domain.displayartwork.application.result;

import java.util.List;

public record DisplayArtworkListResult(List<ArtworkItemResult> artworks) {

  public record ArtworkItemResult(
      Long artworkId,
      String artworkName,
      String artistName,
      Long artistUserId,
      String artworkImageUrl,
      int imageWidth,
      int imageHeight) {}
}
