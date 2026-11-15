package com.example.demo.domain.displayartwork.presentation.request;

import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.global.file.domain.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateDisplayArtworkRequest(
    @NotNull @Positive Long displayId,
    @NotBlank String artworkName,
    String content,
    ArtworkType type,
    @Schema(
            description =
                "작품 분야 최대 3개. 선택 가능한 분야: PAINTING, SCULPTURE, PHOTOGRAPHY, GRAPHIC, BRANDING, PRODUCT, VIDEO, SPACE, ARCHITECTURE, UX_UI, CRAFTS, OTHERS. UXUI/MEDIA/CRAFT/ETC 입력은 UX_UI/VIDEO/CRAFTS/OTHERS로 응답합니다. 기존 DESIGN/FASHION 등은 조회 및 변경 없이 유지할 때만 지원합니다.")
        @Size(max = 3) List<ArtworkType> types,
    @NotNull @Min(1000) int productionYear,
    @Schema(description = "선택 입력. 생략, null 또는 공백이면 저장된 재료·매체를 비웁니다.") String materialMedia,
    String size,
    String point,
    @NotEmpty @Valid List<@NotNull ImageRequest> images,
    @NotBlank String artistName,
    Long artistUserId,
    @Valid @NotNull CoAuthorsRequest coAuthors,
    @NotEmpty List<@NotNull @Positive Long> qaHandlerUserIds,
    @Schema(
            description =
                "대표 사진 또는 영상 썸네일의 비율 크롭. 생략/null이면 같은 표시 이미지에서 유지하고 이미지 변경 시 초기화합니다. 전체 영역은 {x:0,y:0,w:1,h:1}입니다.")
        @Valid ArtworkCropRequest artworkCrop) {

  /**
   * 실제로 사용할 분야 목록.
   *
   * <p>types를 보내면 그 값을 쓰고, 없으면 기존 단일 필드 type을 1개짜리 목록으로 취급한다. 프론트가 types로 옮기는 동안 두 형태를 모두 받기 위한
   * 것이다.
   */
  public List<ArtworkType> resolvedTypes() {
    if (types != null && !types.isEmpty()) {
      return types;
    }
    return type == null ? List.of() : List.of(type);
  }

  @Schema(name = "CreateArtworkMediaRequest")
  public record ImageRequest(
      @NotBlank String imageUrl,
      boolean isThumbnail,
      @NotNull ArtworkImageType imageType,
      @PositiveOrZero int sortOrder,
      String caption,
      @Positive int width,
      @Positive int height,
      @Schema(description = "생략하면 IMAGE. VIDEO의 width와 height는 썸네일 크기입니다.") MediaType mediaType,
      @Schema(description = "VIDEO일 때 필수인 썸네일 이미지 URL") String thumbnailUrl) {}

  public record CoAuthorsRequest(List<@NotNull Long> userIds, List<@NotBlank String> rawNames) {

    public List<Long> userIds() {
      return userIds == null ? List.of() : userIds;
    }

    public List<String> rawNames() {
      return rawNames == null ? List.of() : rawNames;
    }
  }
}
