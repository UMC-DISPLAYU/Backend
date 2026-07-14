package com.example.demo.domain.displayartwork.application.result;

import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import java.util.List;

public record DisplayArtworkResult(
    Long artworkId,
    Long displayId,
    String artworkName,
    String content,
    String type,
    int productionYear,
    String materialMedia,
    String size,
    String point,
    int workSortOrder,
    List<ImageResult> images) {

  public static DisplayArtworkResult from(DisplayArtwork displayArtwork) {
    return new DisplayArtworkResult(
        displayArtwork.getId(),
        displayArtwork.getDisplay().getId(),
        displayArtwork.getArtworkName(),
        displayArtwork.getContent(),
        displayArtwork.getType().name(),
        displayArtwork.getProductionYear(),
        displayArtwork.getMaterialMedia(),
        displayArtwork.getSize(),
        displayArtwork.getPoint(),
        displayArtwork.getWorkSortOrder(),
        displayArtwork.getImages().stream().map(ImageResult::from).toList());
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

    private static ImageResult from(ArtworkImage image) {
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
