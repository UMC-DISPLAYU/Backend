package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkFeelingReplyResponse(
    Long personalFeelingReplyId,
    LocalDateTime createdAt,
    String content,
    Long personalFeelingId,
    Long userId,
    String nickname,
    List<ImageResponse> images) {

  public record ImageResponse(
      Long personalFeelingReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
