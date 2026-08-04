package com.example.demo.domain.personalartworkcommunication.presentation.response;

import com.example.demo.domain.personalartworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkQuestionListResponse(
    List<PersonalArtworkQuestionItemResponse> questions,
    Long nextCursorId,
    int size,
    boolean hasNext) {

  public record PersonalArtworkQuestionItemResponse(
      Long personalQuestionId,
      String content,
      Boolean isPublic,
      boolean accessible,
      boolean canReply,
      Long likeCount,
      AnswerStatus answerStatus,
      LocalDateTime createdAt,
      PersonalArtworkQuestionUserResponse user,
      PersonalArtworkQuestionReplyItemResponse reply) {}

  public record PersonalArtworkQuestionUserResponse(Long userId, String nickname) {}

  public record PersonalArtworkQuestionReplyItemResponse(
      Long personalQuestionReplyId,
      Long userId,
      String nickname,
      Boolean isCreator,
      String content,
      LocalDateTime createdAt,
      Long likeCount) {}
}
