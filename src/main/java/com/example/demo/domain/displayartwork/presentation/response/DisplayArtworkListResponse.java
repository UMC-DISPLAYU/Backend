package com.example.demo.domain.displayartwork.presentation.response;

import java.util.List;

public record DisplayArtworkListResponse(List<ArtworkItemResponse> artworks) {

  public record ArtworkItemResponse(
      Long artworkId,
      String artworkName,
      String artistName,
      String artworkImageUrl,
      int imageWidth,
      int imageHeight) {}
}
