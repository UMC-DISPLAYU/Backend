package com.example.demo.domain.personalartwork.presentation.request;

import com.example.demo.domain.personalartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.personalartwork.domain.type.ArtworkType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PersonalArtworkRequest(
    @NotBlank String artworkName,
    String content,
    ArtworkType type,
    @Size(max = 2) List<ArtworkType> types,
    @NotNull @Min(1000) int productionYear,
    @NotBlank String materialMedia,
    String size,
    String point,
    @NotEmpty @Valid List<ImageRequest> images) {

  /** types를 보내면 그 값을 쓰고, 없으면 기존 단일 필드 type을 1개짜리 목록으로 취급한다. */
  public List<ArtworkType> resolvedTypes() {
    if (types != null && !types.isEmpty()) {
      return types;
    }
    return type == null ? List.of() : List.of(type);
  }

  public record ImageRequest(
      @NotBlank String imageUrl,
      boolean isThumbnail,
      @NotNull ArtworkImageType imageType,
      @PositiveOrZero int sortOrder,
      String caption,
      @Positive int width,
      @Positive int height) {}
}
