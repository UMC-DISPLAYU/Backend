package com.example.demo.domain.personalartworkcommunication.presentation.response;

import com.example.demo.domain.personalartworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkQuestionResponse(
    Long personalQuestionId,
    String content,
    Boolean isPublic,
    AnswerStatus answerStatus,
    LocalDateTime createdAt,
    Long userId,
    List<ImageResponse> images) {

  public record ImageResponse(
      Long personalQuestionImageId, String imageUrl, int width, int height, int sortOrder) {}
}
