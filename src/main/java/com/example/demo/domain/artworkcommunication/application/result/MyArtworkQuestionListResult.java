package com.example.demo.domain.artworkcommunication.application.result;

import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;
import java.util.List;

public record MyArtworkQuestionListResult(
    List<MyArtworkQuestionItemResult> questions, String nextCursor, int size, boolean hasNext) {

  public record MyArtworkQuestionItemResult(
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
