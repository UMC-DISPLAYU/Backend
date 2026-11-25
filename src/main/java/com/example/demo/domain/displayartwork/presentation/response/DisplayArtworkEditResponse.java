package com.example.demo.domain.displayartwork.presentation.response;

import com.example.demo.domain.displayartwork.domain.type.ArtworkFit;
import com.example.demo.global.file.domain.MediaType;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    ArtworkCropResponse artworkCrop,
    @Schema(
            description = "대표 사진 표시 방식. FILL: 꽉 채우기, FIT: 전체 보이기",
            allowableValues = {"FILL", "FIT"})
        ArtworkFit artworkFit,
    @Schema(description = "작품 2부터의 정보. 배열 순서는 작품 표시 순서입니다.") List<ArtworkPartResponse> parts) {

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
      String thumbnailUrl,
      @Schema(
              description = "ARTWORK의 작품 순서. 0은 작품 1, 1은 parts의 첫 항목입니다. WORK_PROCESS에서는 생략됩니다.",
              example = "0")
          @JsonInclude(JsonInclude.Include.NON_NULL)
          Integer partIndex) {}
}
