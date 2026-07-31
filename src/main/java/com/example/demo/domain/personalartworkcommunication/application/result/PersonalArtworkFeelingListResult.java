package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkFeelingListResult(
    List<PersonalArtworkFeelingItemResult> feelings, Long nextCursorId, int size, boolean hasNext) {

  public record PersonalArtworkFeelingItemResult(
      Long personalFeelingId,
      String content,
      LocalDateTime createdAt,
      PersonalArtworkFeelingUserResult user,
      List<ImageResult> images,
      long likeCount,
      long replyCount) {}

  public record PersonalArtworkFeelingUserResult(Long userId, String nickname, Boolean isCreator) {}

  public record ImageResult(
      Long personalFeelingImageId, String imageUrl, int width, int height, int sortOrder) {}
}
