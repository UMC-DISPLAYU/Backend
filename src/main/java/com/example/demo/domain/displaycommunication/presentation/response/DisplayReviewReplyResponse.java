package com.example.demo.domain.displaycommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record DisplayReviewReplyResponse(
    Long displayReviewReplyId,
    LocalDateTime createdAt,
    String content,
    Long displayReviewId,
    Long userId,
    String nickname,
    boolean isTeamMember,
    List<ImageResponse> images) {

  public record ImageResponse(
      Long displayReviewReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
