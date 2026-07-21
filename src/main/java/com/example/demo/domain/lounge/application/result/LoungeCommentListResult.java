package com.example.demo.domain.lounge.application.result;

import com.example.demo.domain.lounge.application.query.LoungeCommentQueryResult;
import java.time.LocalDateTime;
import java.util.List;

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
    LocalDateTime updatedAt,
    List<LoungeCommentListResult> replies) {

  public static LoungeCommentListResult from(
      LoungeCommentQueryResult comment,
      WriterView writer,
      long likeCount,
      long replyCount,
      boolean isLiked,
      Long viewerUserId,
      List<LoungeCommentListResult> replies) {
    return new LoungeCommentListResult(
        comment.loungeCommentId(),
        comment.parentCommentId(),
        comment.content(),
        comment.commentStatus().name(),
        writer,
        likeCount,
        replyCount,
        isLiked,
        comment.authorUserId().equals(viewerUserId),
        comment.createdAt(),
        comment.updatedAt(),
        replies);
  }
}
