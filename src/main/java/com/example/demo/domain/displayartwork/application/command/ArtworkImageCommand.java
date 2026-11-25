package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.global.file.domain.MediaContent;

public record ArtworkImageCommand(
    MediaContent media,
    boolean isThumbnail,
    ArtworkImageType imageType,
    int sortOrder,
    String caption,
    int width,
    int height,
    Integer partIndex) {
  public ArtworkImageCommand(
      String imageUrl,
      boolean isThumbnail,
      ArtworkImageType imageType,
      int sortOrder,
      String caption,
      int width,
      int height) {
    this(
        MediaContent.image(imageUrl),
        isThumbnail,
        imageType,
        sortOrder,
        caption,
        width,
        height,
        null);
  }
}
