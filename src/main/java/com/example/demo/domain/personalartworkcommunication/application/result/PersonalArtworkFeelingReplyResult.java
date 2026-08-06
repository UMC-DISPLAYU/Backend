package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkFeelingReplyResult(
    Long personalFeelingReplyId,
    LocalDateTime createdAt,
    String content,
    Long personalFeelingId,
    Long userId,
    String nickname,
    List<ImageResult> images) {

  public record ImageResult(
      Long personalFeelingReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
