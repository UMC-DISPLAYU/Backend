package com.example.demo.domain.displaycommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record DisplayReviewReplyResult(
    Long displayReviewReplyId,
    LocalDateTime createdAt,
    String content,
    Long displayReviewId,
    Long userId,
    String nickname,
    boolean isTeamMember,
    List<ImageResult> images) {

  public record ImageResult(
      Long displayReviewReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
