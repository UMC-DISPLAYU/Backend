package com.example.demo.domain.displayartwork.presentation.response;

import com.example.demo.global.file.domain.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
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
    List<Long> qaHandlerUserIds,
    ArtworkCropResponse artworkCrop) {

  public record CoAuthorResponse(Long userId, String name) {}

  @Schema(name = "DisplayArtworkEditImageResponse")
  public record ImageResponse(
      Long imageId,
      String imageUrl,
      boolean isThumbnail,
      String imageType,
      int sortOrder,
      String caption,
      int width,
      int height,
      MediaType mediaType,
      String thumbnailUrl) {}
}
