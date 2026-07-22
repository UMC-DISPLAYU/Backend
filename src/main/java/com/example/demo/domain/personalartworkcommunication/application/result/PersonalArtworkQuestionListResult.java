package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkQuestionListResult(
    List<PersonalArtworkQuestionItemResult> questions,
    Long nextCursorId,
    int size,
    boolean hasNext) {

  public record PersonalArtworkQuestionItemResult(
      Long personalQuestionId,
      String content,
      Boolean isPublic,
      String answerStatus,
      LocalDateTime createdAt,
      PersonalArtworkQuestionUserResult user,
      PersonalArtworkQuestionReplyItemResult reply) {}

  public record PersonalArtworkQuestionUserResult(Long userId, String nickname) {}

  public record PersonalArtworkQuestionReplyItemResult(
      Long personalQuestionReplyId,
      Long userId,
      String nickname,
      Boolean isCreator,
      String content,
      LocalDateTime createdAt) {}
}
