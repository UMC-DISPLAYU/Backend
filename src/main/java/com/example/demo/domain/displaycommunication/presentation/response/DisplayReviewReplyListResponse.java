package com.example.demo.domain.displaycommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record DisplayReviewReplyListResponse(
    List<ReplyItemResponse> replies, Long nextCursorId, int size, boolean hasNext) {

  public record ReplyItemResponse(
      Long displayReviewReplyId,
      String content,
      LocalDateTime createdAt,
      UserResponse user,
      boolean isTeamMember,
      long likeCount,
      boolean isLiked,
      List<ImageResponse> images) {}

  public record UserResponse(Long userId, String nickname, String profileImageUrl) {}

  public record ImageResponse(
      Long displayReviewReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
