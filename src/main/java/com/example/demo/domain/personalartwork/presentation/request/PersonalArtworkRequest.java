package com.example.demo.domain.personalartwork.presentation.request;

import com.example.demo.domain.personalartwork.application.command.PersonalArtworkContentCommand;
import com.example.demo.domain.personalartwork.application.command.PersonalArtworkImageCommand;
import com.example.demo.domain.personalartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.personalartwork.domain.type.ArtworkType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

  public PersonalArtworkContentCommand toCommand() {
    return new PersonalArtworkContentCommand(
        artworkName,
        content,
        type,
        productionYear,
        materialMedia,
        size,
        point,
        images.stream().map(ImageRequest::toCommand).toList());
  }

  public record ImageRequest(
      @NotBlank String imageUrl,
      boolean isThumbnail,
      @NotNull ArtworkImageType imageType,
      @PositiveOrZero int sortOrder,
      String caption,
      @NotNull @PositiveOrZero int width,
      @NotNull @PositiveOrZero int height) {

    private PersonalArtworkImageCommand toCommand() {
      return new PersonalArtworkImageCommand(
          imageUrl, isThumbnail, imageType, sortOrder, caption, width, height);
    }
  }
}
