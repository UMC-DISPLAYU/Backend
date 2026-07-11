package com.example.demo.domain.lounge.application.result;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import java.time.LocalDateTime;

public record LoungePostDetailResult(
    Long loungePostId,
    Long authorUserId,
    String title,
    String postImageUrl,
    String content,
    String category,
    String status,
    long likeCount,
    long commentCount,
    long scrapCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static LoungePostDetailResult from(
      LoungePost loungePost, long likeCount, long commentCount, long scrapCount) {
    return new LoungePostDetailResult(
        loungePost.getId(),
        loungePost.getAuthorUserId().value(),
        loungePost.getTitle(),
        loungePost.getPostImageUrl(),
        loungePost.getContent(),
        loungePost.getCategory().name(),
        loungePost.getStatus().name(),
        likeCount,
        commentCount,
        scrapCount,
        loungePost.getCreatedAt(),
        loungePost.getUpdatedAt());
  }
}
