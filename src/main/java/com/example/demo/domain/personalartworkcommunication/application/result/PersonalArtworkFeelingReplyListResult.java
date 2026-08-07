package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkFeelingReplyListResult(
    List<PersonalArtworkFeelingReplyItemResult> replies,
    Long nextCursorId,
    int size,
    boolean hasNext) {

  public record PersonalArtworkFeelingReplyItemResult(
      Long personalFeelingReplyId,
      String content,
      LocalDateTime createdAt,
      PersonalArtworkFeelingReplyUserResult user,
      long likeCount,
      boolean isLiked,
      List<ImageResult> images) {}

  public record PersonalArtworkFeelingReplyUserResult(
      Long userId, String nickname, String profileImageUrl, Boolean isCreator) {}

  public record ImageResult(
      Long personalFeelingReplyImageId, String imageUrl, int width, int height, int sortOrder) {}
}
