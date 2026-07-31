package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record ArtworkFeelingResponse(
    Long feelingId,
    Long userId,
    String content,
    LocalDateTime createdAt,
    List<ImageResponse> images) {

  public record ImageResponse(
      Long feelingImageId, String imageUrl, int width, int height, int sortOrder) {}
}
