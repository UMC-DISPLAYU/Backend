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
      long likeCount) {}

  public record PersonalArtworkFeelingReplyUserResponse(
      Long userId, String nickname, Boolean isCreator) {}
}
