package com.example.demo.domain.personalartwork.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkResponse(
    Long personalArtworkId,
    Long userId,
    String artworkName,
    String content,
    String type,
    int productionYear,
    String materialMedia,
    String size,
    String point,
    LocalDateTime createdAt,
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
