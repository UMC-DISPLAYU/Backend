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

public record UpdateDisplayArtworkRequest(
    @NotBlank String artworkName,
    String content,
    ArtworkType type,
    @Size(max = 2) List<ArtworkType> types,
    @NotNull @Min(1000) int productionYear,
    @NotBlank String materialMedia,
    String size,
    String point,
    @NotEmpty @Valid List<@NotNull ImageRequest> images,
    @NotBlank String artistName,
    Long artistUserId,
    @Valid @NotNull CoAuthorsRequest coAuthors,
    @NotEmpty List<@NotNull @Positive Long> qaHandlerUserIds) {

  /** 등록과 같은 규칙으로, types가 있으면 그것을 쓰고 없으면 기존 단일 type을 1개짜리 목록으로 취급한다. */
  public List<ArtworkType> resolvedTypes() {
    if (types != null && !types.isEmpty()) {
      return types;
    }
    return type == null ? List.of() : List.of(type);
  }

  @Schema(name = "UpdateArtworkMediaRequest")
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
