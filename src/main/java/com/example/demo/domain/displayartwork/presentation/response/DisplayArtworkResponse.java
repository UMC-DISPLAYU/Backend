package com.example.demo.domain.displayartwork.presentation.response;

import java.util.List;

public record DisplayArtworkResponse(
    Long artworkId,
    Long displayId,
    String artworkName,
    String content,
    String type,
    int productionYear,
    String materialMedia,
    String size,
    String point,
    int workSortOrder,
    List<ImageResponse> images) {

  public record ImageResponse(
      Long imageId,
      String imageUrl,
      boolean isThumbnail,
      String imageType,
      int sortOrder,
      String caption,
      int width,
      int height) {}
}
