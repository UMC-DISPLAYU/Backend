package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record ArtworkQuestionReplyResponse(
    Long queReplyId,
    String content,
    LocalDateTime createdAt,
    Long questionId,
    Long creatorId,
    String creatorName,
    List<ImageResponse> images) {

  public record ImageResponse(
      Long questionReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
