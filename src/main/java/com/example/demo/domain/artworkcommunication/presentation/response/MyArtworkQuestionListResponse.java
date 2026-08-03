package com.example.demo.domain.artworkcommunication.presentation.response;

import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;
import java.util.List;

public record MyArtworkQuestionListResponse(
    List<MyArtworkQuestionResponse> questions, String nextCursor, int size, boolean hasNext) {

  public record MyArtworkQuestionResponse(
      Long questionId,
      Long personalQuestionId,
      Long artworkId,
      Long personalArtworkId,
      String artworkName,
      String content,
      Boolean isPublic,
      AnswerStatus answerStatus,
      LocalDateTime createdAt) {}
}
