package com.example.demo.domain.displaycommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record DisplayReviewListResult(
    List<DisplayReviewItemResult> reviews, Long nextCursorId, int size, boolean hasNext) {

  public record DisplayReviewItemResult(
      Long displayReviewId,
      String content,
      LocalDateTime createdAt,
      UserResult user,
      List<ImageResult> images,
      long likeCount,
      long replyCount) {}

  public record UserResult(Long userId, String nickname, String profileImageUrl) {}

  public record ImageResult(
      Long reviewImageId, String imageUrl, int width, int height, int sortOrder) {}
}
