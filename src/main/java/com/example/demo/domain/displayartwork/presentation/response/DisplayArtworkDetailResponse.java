package com.example.demo.domain.displayartwork.presentation.response;

import com.example.demo.domain.displayartwork.domain.type.ArtworkFit;
import com.example.demo.global.file.domain.MediaType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record DisplayArtworkDetailResponse(
    Long artworkId,
    String artworkName,
    String content,
    String type,
    List<String> types,
    int productionYear,
    String size,
    String materialMedia,
    String point,
    List<ImageResponse> images,
    String artistName,
    Long artistUserId,
    List<CoAuthorResponse> coAuthors,
    List<QaHandlerResponse> qaHandlers,
    ExhibitionInfoResponse exhibitionInfo,
    long likeCount,
    boolean isLiked,
    boolean isArchived,
    ArtworkCropResponse artworkCrop,
    @Schema(
            description = "대표 사진 표시 방식. FILL: 꽉 채우기, FIT: 전체 보이기",
            allowableValues = {"FILL", "FIT"})
        ArtworkFit artworkFit,
    @Schema(description = "작품 2부터의 정보. 배열 순서는 작품 표시 순서입니다.") List<ArtworkPartResponse> parts) {

  public record QaHandlerResponse(Long userId, String name) {}

  /** 계정 없이 이름만 입력한 공동 작업자는 userId가 null이다. 프로필 이동 가능 여부 판단에 사용한다. */
  public record CoAuthorResponse(Long userId, String name) {}

  @Schema(name = "DisplayArtworkDetailImageResponse")
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

  public record ExhibitionInfoResponse(
      Long displayId,
      String exhibitionTitle,
      String exhibitionSubtitle,
      String exhibitionThumbnailUrl,
      String exhibitionOrganizer,
      String exhibitionPeriod,
      String exhibitionLocation) {}
}
