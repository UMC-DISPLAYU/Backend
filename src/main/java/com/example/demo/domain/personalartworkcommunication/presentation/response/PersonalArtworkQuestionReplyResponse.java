package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkQuestionReplyResponse(
    Long personalQuestionReplyId,
    LocalDateTime createdAt,
    String content,
    Long personalQuestionId,
    Long userId,
    String nickname,
    Boolean isCreator,
    List<ImageResponse> images) {

  public record ImageResponse(
      Long personalQuestionReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
