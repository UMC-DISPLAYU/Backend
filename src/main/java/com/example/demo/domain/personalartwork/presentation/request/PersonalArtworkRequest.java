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
import java.util.List;

public record PersonalArtworkRequest(
    @NotBlank String artworkName,
    String content,
    @NotNull ArtworkType type,
    @NotNull @Min(1000) int productionYear,
    @NotBlank String materialMedia,
    String size,
    String point,
    @NotEmpty @Valid List<ImageRequest> images) {

  public record ImageRequest(
      @NotBlank String imageUrl,
      boolean isThumbnail,
      @NotNull ArtworkImageType imageType,
      @PositiveOrZero int sortOrder,
      String caption,
      @Positive int width,
      @Positive int height) {}
}
