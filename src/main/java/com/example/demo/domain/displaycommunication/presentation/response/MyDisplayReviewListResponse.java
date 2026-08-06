package com.example.demo.domain.displaycommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record MyDisplayReviewListResponse(
    List<MyDisplayReviewResponse> reviews, Long nextCursorId, int size, boolean hasNext) {

  public record MyDisplayReviewResponse(
      Long displayReviewId,
      Long displayId,
      String displayName,
      String content,
      LocalDateTime createdAt) {}
}
