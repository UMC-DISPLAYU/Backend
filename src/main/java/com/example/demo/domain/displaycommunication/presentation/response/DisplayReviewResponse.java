package com.example.demo.domain.displaycommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record DisplayReviewResponse(
    Long displayReviewId,
    String content,
    LocalDateTime createdAt,
    Long displayId,
    Long userId,
    List<ImageResponse> images) {
  public record ImageResponse(
      Long reviewImageId, String imageUrl, int width, int height, int sortOrder) {}
}
