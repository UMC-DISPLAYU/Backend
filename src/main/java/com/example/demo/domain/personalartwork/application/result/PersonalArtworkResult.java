package com.example.demo.domain.personalartwork.application.result;

import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkImage;
import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkResult(
    Long personalArtworkId,
    Long userId,
    String artistName,
    String artworkName,
    String content,
    String type,
    int productionYear,
    String materialMedia,
    String size,
    String point,
    LocalDateTime createdAt,
    List<ImageResult> images,
    long likeCount,
    boolean isLiked,
    boolean isArchived) {

  public static PersonalArtworkResult from(
      PersonalArtwork personalArtwork,
      String artistName,
      long likeCount,
      boolean isLiked,
      boolean isArchived) {
    return new PersonalArtworkResult(
        personalArtwork.getId(),
        personalArtwork.getOwnerUserId().value(),
        artistName,
        personalArtwork.getArtworkName(),
        personalArtwork.getContent(),
        personalArtwork.getType().name(),
        personalArtwork.getProductionYear(),
        personalArtwork.getMaterialMedia(),
        personalArtwork.getSize(),
        personalArtwork.getPoint(),
        personalArtwork.getCreatedAt(),
        personalArtwork.getImages().stream().map(ImageResult::from).toList(),
        likeCount,
        isLiked,
        isArchived);
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
