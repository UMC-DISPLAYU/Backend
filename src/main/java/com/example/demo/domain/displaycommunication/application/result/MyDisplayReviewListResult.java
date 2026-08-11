package com.example.demo.domain.displaycommunication.application.result;

import com.example.demo.domain.displaycommunication.application.query.MyDisplayReviewQueryItem;
import java.time.LocalDateTime;
import java.util.List;

public record MyDisplayReviewListResult(
    List<MyDisplayReviewItemResult> reviews, Long nextCursorId, int size, boolean hasNext) {

  public record MyDisplayReviewItemResult(
      Long displayReviewId,
      Long displayId,
      String displayName,
      String content,
      LocalDateTime createdAt) {

    public static MyDisplayReviewItemResult from(
        MyDisplayReviewQueryItem item, String displayName) {
      return new MyDisplayReviewItemResult(
          item.displayReviewId(), item.displayId(), displayName, item.content(), item.createdAt());
    }
  }
}
