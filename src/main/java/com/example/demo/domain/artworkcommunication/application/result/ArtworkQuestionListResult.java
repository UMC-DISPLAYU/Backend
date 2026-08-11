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
      Long likeCount,
      boolean isLiked,
      String answerStatus,
      LocalDateTime createdAt,
      List<QuestionImageResult> images,
      ArtworkQuestionUserResult user,
      ArtworkQuestionReplyItemResult reply) {}

  public record ArtworkQuestionUserResult(Long userId, String nickname, Boolean isCreator) {}

  public record ArtworkQuestionReplyItemResult(
      Long questionReplyId,
      Long creatorId,
      String creatorName,
      Boolean isCreator,
      String content,
      LocalDateTime createdAt,
      List<ReplyImageResult> images,
      Long likeCount,
      boolean isLiked) {}

  public record QuestionImageResult(
      Long questionImageId, String imageUrl, int width, int height, int sortOrder) {}

  public record ReplyImageResult(
      Long questionReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
