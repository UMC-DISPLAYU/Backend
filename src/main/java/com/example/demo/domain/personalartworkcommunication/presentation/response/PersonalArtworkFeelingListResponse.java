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
      PersonalArtworkFeelingUserResponse user,
      long likeCount,
      long replyCount) {}

  public record PersonalArtworkFeelingUserResponse(
      Long userId, String nickname, Boolean isCreator) {}
}
