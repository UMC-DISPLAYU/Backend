package com.example.demo.domain.displaycommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record DisplayReviewResult(
    Long displayReviewId,
    String content,
    LocalDateTime createdAt,
    Long displayId,
    Long userId,
    List<ImageResult> images) {
  public record ImageResult(
      Long reviewImageId, String imageUrl, int width, int height, int sortOrder) {}
}
