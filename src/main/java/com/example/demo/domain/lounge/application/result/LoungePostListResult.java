package com.example.demo.domain.lounge.application.result;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.vo.LoungeWriter;
import java.time.LocalDateTime;

public record LoungePostListResult(
    Long loungePostId,
    LoungeWriter writer,
    String title,
    String postImageUrl,
    String category,
    long likeCount,
    long commentCount,
    boolean isLiked,
    boolean isMyPost,
    LocalDateTime createdAt) {
  public static LoungePostListResult from(
      LoungePost loungePost,
      LoungeWriter writer,
      long likeCount,
      long commentCount,
      boolean isLiked,
      Long viewerUserId) {
    return new LoungePostListResult(
        loungePost.getId(),
        writer,
        loungePost.getTitle(),
        loungePost.getPostImageUrl(),
        loungePost.getCategory().name(),
        likeCount,
        commentCount,
        isLiked,
        loungePost.getAuthorUserId().value().equals(viewerUserId),
        loungePost.getCreatedAt());
  }
}
