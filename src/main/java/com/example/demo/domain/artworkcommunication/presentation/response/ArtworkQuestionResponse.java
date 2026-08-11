package com.example.demo.domain.artworkcommunication.presentation.response;

import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;
import java.util.List;

public record ArtworkQuestionResponse(
    Long questionId,
    String content,
    Boolean isPublic,
    AnswerStatus answerStatus,
    LocalDateTime createdAt,
    Long displayArtworkId,
    Long userId,
    List<ImageResponse> images) {

  public record ImageResponse(
      Long questionImageId, String imageUrl, int width, int height, int sortOrder) {}
}
