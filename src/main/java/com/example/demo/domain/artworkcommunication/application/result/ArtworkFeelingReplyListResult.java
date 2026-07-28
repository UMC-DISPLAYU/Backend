package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record ArtworkFeelingReplyListResult(
    List<ArtworkFeelingReplyItemResult> replies, Long nextCursorId, int size, boolean hasNext) {

  public record ArtworkFeelingReplyItemResult(
      Long feelingReplyId,
      String content,
      LocalDateTime createdAt,
      ArtworkFeelingReplyUserResult user,
      long likeCount) {}

  public record ArtworkFeelingReplyUserResult(Long userId, String nickname, Boolean isCreator) {}
}
