package com.example.demo.domain.lounge.application.result;

import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import java.time.LocalDateTime;

public record LoungeCommentListResult(
    Long loungeCommentId,
    Long parentCommentId,
    String content,
    String commentStatus,
    WriterView writer,
    long likeCount,
    long replyCount,
    boolean isLiked,
    boolean isMyComment,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static LoungeCommentListResult from(
      LoungeComment comment,
      WriterView writer,
      long likeCount,
      long replyCount,
      boolean isLiked,
      Long viewerUserId) {
    return new LoungeCommentListResult(
        comment.getId(),
        comment.getParentCommentId(),
        comment.getContent(),
        comment.getStatus().name(),
        writer,
        likeCount,
        replyCount,
        isLiked,
        comment.getAuthorUserId().value().equals(viewerUserId),
        comment.getCreatedAt(),
        comment.getUpdatedAt());
  }
}
