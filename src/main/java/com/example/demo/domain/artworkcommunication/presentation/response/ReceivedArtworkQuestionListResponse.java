package com.example.demo.domain.artworkcommunication.presentation.response;

import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;
import java.util.List;

public record ReceivedArtworkQuestionListResponse(
    List<ReceivedArtworkQuestionResponse> questions, String nextCursor, int size, boolean hasNext) {

  public record ReceivedArtworkQuestionResponse(
      Long questionId,
      Long personalQuestionId,
      Long artworkId,
      Long personalArtworkId,
      String artworkName,
      String content,
      Boolean isPublic,
      AnswerStatus answerStatus,
      Long questionerId,
      String questionerNickname,
      LocalDateTime createdAt) {}
}
