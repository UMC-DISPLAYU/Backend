package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record ArtworkFeelingReplyResult(
    Long feelingReplyId,
    LocalDateTime createdAt,
    String content,
    Long feelingId,
    Long userId,
    String nickname,
    List<ImageResult> images) {

  public record ImageResult(
      Long feelingReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
