package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkFeelingListResponse(
    List<PersonalArtworkFeelingItemResponse> feelings,
    Long nextCursorId,
    int size,
    boolean hasNext) {

  public record PersonalArtworkFeelingItemResponse(
      Long personalFeelingId,
      String content,
      LocalDateTime createdAt,
      boolean isDeleted,
      boolean isMine,
      PersonalArtworkFeelingUserResponse user,
      List<ImageResponse> images,
      long likeCount,
      boolean isLiked,
      long replyCount) {}

  public record PersonalArtworkFeelingUserResponse(
      Long userId, String nickname, Boolean isCreator) {}

  public record ImageResponse(
      Long personalFeelingImageId, String imageUrl, int width, int height, int sortOrder) {}
}
