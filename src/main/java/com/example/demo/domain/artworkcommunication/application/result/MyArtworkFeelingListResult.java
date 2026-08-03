package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record MyArtworkFeelingListResult(
    List<MyArtworkFeelingItemResult> feelings, String nextCursor, int size, boolean hasNext) {

  public record MyArtworkFeelingItemResult(
      Long artworkId,
      Long personalArtworkId,
      String artworkName,
      String content,
      LocalDateTime createdAt) {}
}
