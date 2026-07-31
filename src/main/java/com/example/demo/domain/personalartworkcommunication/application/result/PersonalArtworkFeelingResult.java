package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkFeelingResult(
    Long personalFeelingId,
    Long userId,
    String content,
    LocalDateTime createdAt,
    List<ImageResult> images) {

  public record ImageResult(
      Long personalFeelingImageId, String imageUrl, int width, int height, int sortOrder) {}
}
