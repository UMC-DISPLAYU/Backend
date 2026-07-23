package com.example.demo.domain.lounge.application.result;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import java.time.LocalDateTime;
import java.util.List;

public record LoungePostDetailResult(
    Long loungePostId,
    WriterView writer,
    String title,
    List<String> postImageUrls,
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
      WriterView writer,
      long likeCount,
      long commentCount,
      boolean isLiked,
      boolean isScrapped,
      Long viewerUserId) {
    return new LoungePostDetailResult(
        loungePost.getId(),
        writer,
        loungePost.getTitle(),
        loungePost.getPostImageUrls(),
        loungePost.getContent(),
        loungePost.getCategory().name(),
        loungePost.getStatus().name(),
        likeCount,
        commentCount,
        isLiked,
        isScrapped,
        loungePost.isAuthoredBy(viewerUserId),
        loungePost.getCreatedAt(),
        loungePost.getUpdatedAt());
  }
}
