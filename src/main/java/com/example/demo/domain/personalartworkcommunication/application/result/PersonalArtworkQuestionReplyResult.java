package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkQuestionReplyResult(
    Long personalQuestionReplyId,
    LocalDateTime createdAt,
    String content,
    Long personalQuestionId,
    Long userId,
    String nickname,
    Boolean isCreator,
    List<ImageResult> images) {

  public record ImageResult(
      Long personalQuestionReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
