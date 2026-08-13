package com.example.demo.domain.displayartwork.presentation.request;

import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
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
    Field type,
    @Size(max = 2) List<Field> types,
    @NotNull @Min(1000) int productionYear,
    @NotBlank String materialMedia,
    String size,
    String point,
    @NotEmpty @Valid List<ImageRequest> images,
    @NotBlank String artistName,
    Long artistUserId,
    @Valid @NotNull CoAuthorsRequest coAuthors,
    @NotEmpty List<@NotNull @Positive Long> qaHandlerUserIds) {

  /**
   * 실제로 사용할 분야 목록.
   *
   * <p>types를 보내면 그 값을 쓰고, 없으면 기존 단일 필드 type을 1개짜리 목록으로 취급한다. 프론트가 types로 옮기는 동안 두 형태를 모두 받기 위한
   * 것이다.
   */
  public List<Field> resolvedTypes() {
    if (types != null && !types.isEmpty()) {
      return types;
    }
    return type == null ? List.of() : List.of(type);
  }

  public enum Field {
    PAINTING,
    DESIGN,
    PHOTOGRAPHY,
    ARCHITECTURE,
    MEDIA,
    CRAFT,
    SCULPTURE,
    FASHION,
    COMPLEX,
    ETC
  }

  public record ImageRequest(
      @NotBlank String imageUrl,
      boolean isThumbnail,
      @NotNull ArtworkImageType imageType,
      @PositiveOrZero int sortOrder,
      String caption,
      @Positive int width,
      @Positive int height) {}

  public record CoAuthorsRequest(List<@NotNull Long> userIds, List<@NotBlank String> rawNames) {

    public List<Long> userIds() {
      return userIds == null ? List.of() : userIds;
    }

    public List<String> rawNames() {
      return rawNames == null ? List.of() : rawNames;
    }
  }
}
