package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record ArtworkQuestionListResult(
    List<ArtworkQuestionItemResult> questions, Long nextCursorId, int size, boolean hasNext) {

  public record ArtworkQuestionItemResult(
      Long questionId,
      String content,
      Boolean isPublic,
      boolean accessible,
      boolean canReply,
      String answerStatus,
      LocalDateTime createdAt,
      ArtworkQuestionUserResult user,
      ArtworkQuestionReplyItemResult reply) {}

  public record ArtworkQuestionUserResult(Long userId, String nickname) {}

  public record ArtworkQuestionReplyItemResult(
      Long creatorId,
      String creatorName,
      Boolean isCreator,
      String content,
      LocalDateTime createdAt) {}
}
