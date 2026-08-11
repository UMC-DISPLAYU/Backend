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
      boolean isMine,
      boolean canReply,
      Long likeCount,
      boolean isLiked,
      String answerStatus,
      LocalDateTime createdAt,
      List<QuestionImageResponse> images,
      ArtworkQuestionUserResponse user,
      ArtworkQuestionReplyItemResponse reply) {}

  public record ArtworkQuestionUserResponse(Long userId, String nickname, Boolean isCreator) {}

  public record ArtworkQuestionReplyItemResponse(
      Long questionReplyId,
      Long creatorId,
      String creatorName,
      Boolean isCreator,
      String content,
      LocalDateTime createdAt,
      List<ReplyImageResponse> images,
      Long likeCount,
      boolean isLiked,
      boolean isMine) {}

  public record QuestionImageResponse(
      Long questionImageId, String imageUrl, int width, int height, int sortOrder) {}

  public record ReplyImageResponse(
      Long questionReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
