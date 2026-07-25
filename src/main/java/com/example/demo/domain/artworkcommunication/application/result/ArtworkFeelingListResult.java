package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record ArtworkFeelingListResult(
    List<ArtworkFeelingItemResult> feelings, Long nextCursorId, int size, boolean hasNext) {

  public record ArtworkFeelingItemResult(
      Long feelingId,
      String content,
      LocalDateTime createdAt,
      ArtworkFeelingUserResult user,
      List<ArtworkFeelingReplyItemResult> replies) {}

  public record ArtworkFeelingUserResult(Long userId, String nickname, Boolean isCreator) {}

  public record ArtworkFeelingReplyItemResult(
      Long userId, String nickname, String content, LocalDateTime createdAt, Boolean isCreator) {}
}
