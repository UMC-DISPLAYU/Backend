package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record ArtworkFeelingListResult(
    List<ArtworkFeelingItemResult> feelings, Long nextCursorId, int size, boolean hasNext) {

  public record ArtworkFeelingItemResult(
      Long feelingId,
      String content,
      LocalDateTime createdAt,
      boolean isDeleted,
      boolean isMine,
      ArtworkFeelingUserResult user,
      List<ImageResult> images,
      long likeCount,
      boolean isLiked,
      long replyCount) {}

  public record ArtworkFeelingUserResult(Long userId, String nickname, Boolean isCreator) {}

  public record ImageResult(
      Long feelingImageId, String imageUrl, int width, int height, int sortOrder) {}
}
