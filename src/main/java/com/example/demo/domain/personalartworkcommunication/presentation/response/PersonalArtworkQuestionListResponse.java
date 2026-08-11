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
      boolean isMine,
      boolean canReply,
      Long likeCount,
      boolean isLiked,
      AnswerStatus answerStatus,
      LocalDateTime createdAt,
      List<QuestionImageResponse> images,
      PersonalArtworkQuestionUserResponse user,
      PersonalArtworkQuestionReplyItemResponse reply) {}

  public record PersonalArtworkQuestionUserResponse(
      Long userId, String nickname, Boolean isCreator) {}

  public record PersonalArtworkQuestionReplyItemResponse(
      Long personalQuestionReplyId,
      Long userId,
      String nickname,
      Boolean isCreator,
      String content,
      LocalDateTime createdAt,
      List<ReplyImageResponse> images,
      Long likeCount,
      boolean isLiked,
      boolean isMine) {}

  public record QuestionImageResponse(
      Long personalQuestionImageId, String imageUrl, int width, int height, int sortOrder) {}

  public record ReplyImageResponse(
      Long personalQuestionReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
