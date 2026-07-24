package com.example.demo.domain.displaycommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record DisplayReviewListResponse(
    List<DisplayReviewItemResponse> reviews, Long nextCursorId, int size, boolean hasNext) {

  public record DisplayReviewItemResponse(
      Long displayReviewId,
      String content,
      LocalDateTime createdAt,
      UserResponse user,
      List<ImageResponse> images,
      long likeCount,
      long replyCount) {}

  public record UserResponse(Long userId, String nickname, String profileImageUrl) {}

  public record ImageResponse(
      Long reviewImageId, String imageUrl, int width, int height, int sortOrder) {}
}
