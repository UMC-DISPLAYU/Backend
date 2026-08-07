package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record ArtworkFeelingReplyResponse(
    Long feelingReplyId,
    LocalDateTime createdAt,
    String content,
    Long feelingId,
    Long userId,
    String nickname,
    List<ImageResponse> images) {

  public record ImageResponse(
      Long feelingReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
