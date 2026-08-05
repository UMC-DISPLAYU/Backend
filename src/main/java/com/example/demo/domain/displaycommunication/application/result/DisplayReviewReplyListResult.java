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
      boolean isLiked) {}

  public record UserResult(Long userId, String nickname, String profileImageUrl) {}
}
