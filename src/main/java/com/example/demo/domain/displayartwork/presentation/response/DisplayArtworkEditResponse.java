package com.example.demo.domain.displayartwork.presentation.response;

import java.util.List;

public record DisplayArtworkEditResponse(
    Long artworkId,
    Long displayId,
    String artworkName,
    String content,
    String type,
    List<String> types,
    int productionYear,
    String materialMedia,
    String size,
    String point,
    List<ImageResponse> images,
    String artistName,
    Long artistUserId,
    List<CoAuthorResponse> coAuthors,
    List<Long> qaHandlerUserIds) {

  public record CoAuthorResponse(Long userId, String name) {}

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
