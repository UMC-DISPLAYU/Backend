package com.example.demo.domain.displayartwork.presentation.response;

import java.util.List;

public record DisplayArtworkDetailResponse(
    Long artworkId,
    String artworkName,
    String content,
    String type,
    int productionYear,
    String size,
    String materialMedia,
    String point,
    List<ImageResponse> images,
    String artistName,
    Long artistUserId,
    ExhibitionInfoResponse exhibitionInfo,
    long likeCount,
    boolean isLiked,
    boolean isSaved) {

  public record ImageResponse(
      Long imageId,
      String imageUrl,
      boolean isThumbnail,
      String imageType,
      int sortOrder,
      String caption,
      int width,
      int height) {}

  public record ExhibitionInfoResponse(
      Long displayId, String exhibitionTitle, String exhibitionPeriod, String exhibitionLocation) {}
}
