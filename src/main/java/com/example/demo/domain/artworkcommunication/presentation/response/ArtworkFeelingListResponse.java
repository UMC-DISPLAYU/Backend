package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record ArtworkFeelingListResponse(
    List<ArtworkFeelingItemResponse> feelings, Long nextCursorId, int size, boolean hasNext) {

  public record ArtworkFeelingItemResponse(
      Long feelingId,
      String content,
      LocalDateTime createdAt,
      boolean isDeleted,
      boolean isMine,
      ArtworkFeelingUserResponse user,
      List<ImageResponse> images,
      long likeCount,
      boolean isLiked,
      long replyCount) {}

  public record ArtworkFeelingUserResponse(
      Long userId, String nickname, String profileImageUrl, Boolean isCreator) {}

  public record ImageResponse(
      Long feelingImageId, String imageUrl, int width, int height, int sortOrder) {}
}
