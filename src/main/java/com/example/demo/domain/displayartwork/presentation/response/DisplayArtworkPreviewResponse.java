package com.example.demo.domain.displayartwork.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record DisplayArtworkPreviewResponse(
    List<ArtworkCardResponse> artworks, int page, int size, boolean isLast) {

  public record ArtworkCardResponse(
      Long artworkId,
      String artworkName,
      String artistName,
      Long artistUserId,
      String artworkImageUrl,
      int imageWidth,
      int imageHeight,
      ExhibitionInfoResponse exhibitionInfo,
      @Schema(description = "대표 작가가 첫 번째이며 공동 작가가 이어집니다. Q&A 전용 담당자는 제외합니다.")
          List<ArtistResponse> artists) {}

  public record ArtistResponse(
      @Schema(
              description = "작가 프로필 연결용 사용자 ID. 이름만 등록된 작가는 null입니다.",
              types = {"integer", "null"})
          Long userId,
      String name) {}

  public record ExhibitionInfoResponse(
      Long displayId, String exhibitionTitle, String exhibitionPeriod, String exhibitionLocation) {}
}
