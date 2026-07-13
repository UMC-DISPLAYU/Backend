package com.example.demo.domain.personalartwork.application.result;

import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkImage;
import java.util.List;

public record PersonalArtworkResult(
    Long personalArtworkId,
    Long userId,
    String artworkName,
    String content,
    String type,
    int productionYear,
    String materialMedia,
    String size,
    String point,
    int sortOrder,
    List<ImageResult> images) {

  public static PersonalArtworkResult from(PersonalArtwork personalArtwork) {
    return new PersonalArtworkResult(
        personalArtwork.getId(),
        personalArtwork.getOwnerUserId().value(),
        personalArtwork.getArtworkName(),
        personalArtwork.getContent(),
        personalArtwork.getType().name(),
        personalArtwork.getProductionYear(),
        personalArtwork.getMaterialMedia(),
        personalArtwork.getSize(),
        personalArtwork.getPoint(),
        personalArtwork.getSortOrder(),
        personalArtwork.getImages().stream()
            .filter(image -> !image.isDeleted())
            .map(ImageResult::from)
            .toList());
  }

  public record ImageResult(
      Long imageId,
      String imageUrl,
      boolean isThumbnail,
      String imageType,
      int sortOrder,
      String caption,
      int width,
      int height) {

    private static ImageResult from(PersonalArtworkImage image) {
      return new ImageResult(
          image.getId(),
          image.getImageUrl(),
          image.isThumbnail(),
          image.getImageType().name(),
          image.getSortOrder(),
          image.getCaption(),
          image.getWidth(),
          image.getHeight());
    }
  }
}
