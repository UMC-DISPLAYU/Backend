package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record ArtworkFeelingReplyListResponse(
    List<ArtworkFeelingReplyItemResponse> replies, Long nextCursorId, int size, boolean hasNext) {

  public record ArtworkFeelingReplyItemResponse(
      Long feelingReplyId,
      String content,
      LocalDateTime createdAt,
      ArtworkFeelingReplyUserResponse user,
      long likeCount) {}

  public record ArtworkFeelingReplyUserResponse(Long userId, String nickname, Boolean isCreator) {}
}
