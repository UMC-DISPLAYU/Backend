package com.example.demo.domain.lounge.application.result;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import java.time.LocalDateTime;

public record LoungePostListResult(
    Long loungePostId,
    WriterView writer,
    String title,
    String content,
    String postImageUrl,
    String category,
    long likeCount,
    long commentCount,
    boolean isLiked,
    boolean isMyPost,
    LocalDateTime createdAt) {
  public static LoungePostListResult from(
      LoungePost loungePost,
      WriterView writer,
      long likeCount,
      long commentCount,
      boolean isLiked,
      Long viewerUserId) {
    return new LoungePostListResult(
        loungePost.getId(),
        writer,
        loungePost.getTitle(),
        loungePost.getContent(),
        loungePost.getPostImageUrl(),
        loungePost.getCategory().name(),
        likeCount,
        commentCount,
        isLiked,
        loungePost.isAuthoredBy(viewerUserId),
        loungePost.getCreatedAt());
  }
}
