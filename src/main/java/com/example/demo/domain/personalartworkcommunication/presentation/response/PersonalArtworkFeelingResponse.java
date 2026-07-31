package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkFeelingResponse(
    Long personalFeelingId,
    Long userId,
    String content,
    LocalDateTime createdAt,
    List<ImageResponse> images) {

  public record ImageResponse(
      Long personalFeelingImageId, String imageUrl, int width, int height, int sortOrder) {}
}
