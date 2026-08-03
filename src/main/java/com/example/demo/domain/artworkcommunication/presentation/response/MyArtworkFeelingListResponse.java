package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record MyArtworkFeelingListResponse(
    List<MyArtworkFeelingResponse> feelings, String nextCursor, int size, boolean hasNext) {

  public record MyArtworkFeelingResponse(
      Long artworkId,
      Long personalArtworkId,
      String artworkName,
      String content,
      LocalDateTime createdAt) {}
}
