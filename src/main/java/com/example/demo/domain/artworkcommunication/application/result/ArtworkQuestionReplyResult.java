package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record ArtworkQuestionReplyResult(
    Long queReplyId,
    String content,
    LocalDateTime createdAt,
    Long questionId,
    Long creatorId,
    String creatorName,
    List<ImageResult> images) {

  public record ImageResult(
      Long questionReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
