package com.example.demo.domain.personalartworkcommunication.application.result;

import com.example.demo.domain.personalartworkcommunication.domain.type.AnswerStatus;
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
      boolean accessible,
      boolean canReply,
      Long likeCount,
      boolean isLiked,
      AnswerStatus answerStatus,
      LocalDateTime createdAt,
      PersonalArtworkQuestionUserResult user,
      PersonalArtworkQuestionReplyItemResult reply) {}

  public record PersonalArtworkQuestionUserResult(
      Long userId, String nickname, Boolean isCreator) {}

  public record PersonalArtworkQuestionReplyItemResult(
      Long personalQuestionReplyId,
      Long userId,
      String nickname,
      Boolean isCreator,
      String content,
      LocalDateTime createdAt,
      Long likeCount,
      boolean isLiked) {}
}
