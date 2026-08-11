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
      boolean isMine,
      boolean canReply,
      Long likeCount,
      boolean isLiked,
      AnswerStatus answerStatus,
      LocalDateTime createdAt,
      List<QuestionImageResult> images,
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
      List<ReplyImageResult> images,
      Long likeCount,
      boolean isLiked,
      boolean isMine) {}

  public record QuestionImageResult(
      Long personalQuestionImageId, String imageUrl, int width, int height, int sortOrder) {}

  public record ReplyImageResult(
      Long personalQuestionReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
