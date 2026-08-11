package com.example.demo.domain.personalartworkcommunication.application.result;

import com.example.demo.domain.personalartworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkQuestionResult(
    Long personalQuestionId,
    String content,
    Boolean isPublic,
    AnswerStatus answerStatus,
    LocalDateTime createdAt,
    Long userId,
    List<ImageResult> images) {

  public record ImageResult(
      Long personalQuestionImageId, String imageUrl, int width, int height, int sortOrder) {}
}
