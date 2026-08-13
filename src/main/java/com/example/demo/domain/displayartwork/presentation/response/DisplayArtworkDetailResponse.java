package com.example.demo.domain.displayartwork.presentation.response;

import java.util.List;

public record DisplayArtworkDetailResponse(
    Long artworkId,
    String artworkName,
    String content,
    String type,
    List<String> types,
    int productionYear,
    String size,
    String materialMedia,
    String point,
    List<ImageResponse> images,
    String artistName,
    Long artistUserId,
    List<CoAuthorResponse> coAuthors,
    List<QaHandlerResponse> qaHandlers,
    ExhibitionInfoResponse exhibitionInfo,
    long likeCount,
    boolean isLiked,
    boolean isArchived) {

  public record QaHandlerResponse(Long userId, String name) {}

  /** 계정 없이 이름만 입력한 공동 작업자는 userId가 null이다. 프로필 이동 가능 여부 판단에 사용한다. */
  public record CoAuthorResponse(Long userId, String name) {}

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
      Long displayId,
      String exhibitionTitle,
      String exhibitionSubtitle,
      String exhibitionThumbnailUrl,
      String exhibitionOrganizer,
      String exhibitionPeriod,
      String exhibitionLocation) {}
}
