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
      List<PersonalArtworkFeelingReplyItemResponse> replies) {}

  public record PersonalArtworkFeelingUserResponse(Long userId, String nickname) {}

  public record PersonalArtworkFeelingReplyItemResponse(
      Long personalFeelingReplyId,
      Long userId,
      String nickname,
      String content,
      LocalDateTime createdAt,
      Boolean isCreator) {}
}
