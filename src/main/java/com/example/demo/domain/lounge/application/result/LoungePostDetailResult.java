package com.example.demo.domain.lounge.application.result;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.vo.LoungeWriter;
import java.time.LocalDateTime;

public record LoungePostDetailResult(
    Long loungePostId,
    LoungeWriter writer,
    String title,
    String postImageUrl,
    String content,
    String category,
    String postStatus,
    long likeCount,
    long commentCount,
    boolean isLiked,
    boolean isScrapped,
    boolean isMyPost,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static LoungePostDetailResult from(
      LoungePost loungePost,
      LoungeWriter writer,
      long likeCount,
      long commentCount,
      boolean isLiked,
      boolean isScrapped,
      Long viewerUserId) {
    return new LoungePostDetailResult(
        loungePost.getId(),
        writer,
        loungePost.getTitle(),
        loungePost.getPostImageUrl(),
        loungePost.getContent(),
        loungePost.getCategory().name(),
        loungePost.getStatus().name(),
        likeCount,
        commentCount,
        isLiked,
        isScrapped,
        loungePost.getAuthorUserId().value().equals(viewerUserId),
        loungePost.getCreatedAt(),
        loungePost.getUpdatedAt());
  }
}
