package com.example.demo.domain.displayartwork.presentation.request;

import com.example.demo.domain.displayartwork.application.command.ArtworkImageCommand;
import com.example.demo.domain.displayartwork.application.command.CreateDisplayArtworkCommand;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public record CreateDisplayArtworkRequest(
    @NotNull @Positive Long displayId,
    @NotBlank String artworkName,
    @NotBlank String content,
    @NotNull ArtworkType type,
    @NotNull @Min(1000) int productionYear,
    @NotBlank String materialMedia,
    @NotBlank String size,
    @NotBlank String point,
    @NotEmpty @Valid List<ImageRequest> images,
    @NotBlank String artistName,
    Long artistUserId,
    @Valid @NotNull CoAuthorsRequest coAuthors,
    @NotEmpty List<@NotNull @Positive Long> qaHandlerUserIds) {

  public CreateDisplayArtworkCommand toCommand() {
    return new CreateDisplayArtworkCommand(
        displayId,
        artworkName,
        content,
        type,
        productionYear,
        materialMedia,
        size,
        point,
        images.stream().map(ImageRequest::toCommand).toList(),
        artistName,
        artistUserId,
        coAuthors.userIds(),
        coAuthors.rawNames(),
        qaHandlerUserIds);
  }

  public record ImageRequest(
      @NotBlank String imageUrl,
      boolean isThumbnail,
      @NotNull ArtworkImageType imageType,
      @PositiveOrZero int sortOrder,
      String caption,
      @Positive int width,
      @Positive int height) {

    private ArtworkImageCommand toCommand() {
      return new ArtworkImageCommand(
          imageUrl, isThumbnail, imageType, sortOrder, caption, width, height);
    }
  }

  public record CoAuthorsRequest(List<@NotNull Long> userIds, List<@NotBlank String> rawNames) {

    public List<Long> userIds() {
      return userIds == null ? List.of() : userIds;
    }

    public List<String> rawNames() {
      return rawNames == null ? List.of() : rawNames;
    }
  }
}
