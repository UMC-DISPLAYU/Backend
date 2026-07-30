package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record ArtworkFeelingResult(
    Long feelingId,
    Long userId,
    String content,
    LocalDateTime createdAt,
    List<ImageResult> images) {

  public record ImageResult(
      Long feelingImageId, String imageUrl, int width, int height, int sortOrder) {}
}
