package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record ArtworkFeelingReplyListResult(
    List<ArtworkFeelingReplyItemResult> replies, Long nextCursorId, int size, boolean hasNext) {

  public record ArtworkFeelingReplyItemResult(
      Long feelingReplyId,
      String content,
      LocalDateTime createdAt,
      ArtworkFeelingReplyUserResult user,
      long likeCount,
      boolean isLiked,
      List<ImageResult> images) {}

  public record ArtworkFeelingReplyUserResult(
      Long userId, String nickname, String profileImageUrl, Boolean isCreator) {}

  public record ImageResult(
      Long feelingReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
