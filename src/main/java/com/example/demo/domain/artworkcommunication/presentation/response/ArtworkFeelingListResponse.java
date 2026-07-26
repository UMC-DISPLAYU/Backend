package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record ArtworkFeelingListResponse(
    List<ArtworkFeelingItemResponse> feelings, Long nextCursorId, int size, boolean hasNext) {

  public record ArtworkFeelingItemResponse(
      Long feelingId,
      String content,
      LocalDateTime createdAt,
      ArtworkFeelingUserResponse user,
      long likeCount,
      long replyCount) {}

  public record ArtworkFeelingUserResponse(Long userId, String nickname, Boolean isCreator) {}
}
