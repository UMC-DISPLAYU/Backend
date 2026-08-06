package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkFeelingReplyListResponse(
    List<PersonalArtworkFeelingReplyItemResponse> replies,
    Long nextCursorId,
    int size,
    boolean hasNext) {

  public record PersonalArtworkFeelingReplyItemResponse(
      Long personalFeelingReplyId,
      String content,
      LocalDateTime createdAt,
      PersonalArtworkFeelingReplyUserResponse user,
      long likeCount,
      boolean isLiked,
      List<ImageResponse> images) {}

  public record PersonalArtworkFeelingReplyUserResponse(
      Long userId, String nickname, String profileImageUrl, Boolean isCreator) {}

  public record ImageResponse(
      Long personalFeelingReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
