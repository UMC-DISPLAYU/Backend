package com.example.demo.domain.displaycommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record DisplayReviewReplyListResult(
    List<ReplyItemResult> replies, Long nextCursorId, int size, boolean hasNext) {

  public record ReplyItemResult(
      Long displayReviewReplyId,
      String content,
      LocalDateTime createdAt,
      UserResult user,
      boolean isTeamMember,
      long likeCount,
      boolean isLiked,
      List<ImageResult> images) {}

  public record UserResult(Long userId, String nickname, String profileImageUrl) {}

  public record ImageResult(
      Long displayReviewReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
