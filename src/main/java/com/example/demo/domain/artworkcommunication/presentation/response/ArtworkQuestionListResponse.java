package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record ArtworkQuestionListResponse(
    List<ArtworkQuestionItemResponse> questions, Long nextCursorId, int size, boolean hasNext) {

  public record ArtworkQuestionItemResponse(
      Long questionId,
      String content,
      Boolean isPublic,
      boolean accessible,
      boolean canReply,
      Long likeCount,
      String answerStatus,
      LocalDateTime createdAt,
      ArtworkQuestionUserResponse user,
      ArtworkQuestionReplyItemResponse reply) {}

  public record ArtworkQuestionUserResponse(Long userId, String nickname) {}

  public record ArtworkQuestionReplyItemResponse(
      Long questionReplyId,
      Long creatorId,
      String creatorName,
      Boolean isCreator,
      String content,
      LocalDateTime createdAt,
      Long likeCount) {}
}
