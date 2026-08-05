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
      boolean isLiked) {}

  public record UserResponse(Long userId, String nickname, String profileImageUrl) {}
}
