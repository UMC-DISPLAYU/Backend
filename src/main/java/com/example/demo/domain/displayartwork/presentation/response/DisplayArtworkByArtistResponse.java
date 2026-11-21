package com.example.demo.domain.displayartwork.presentation.response;

import com.example.demo.domain.displayartwork.domain.type.ArtworkFit;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

public record DisplayArtworkByArtistResponse(List<ArtworkCardResponse> artworks) {

  public record ArtworkCardResponse(
      Long artworkId,
      String artworkName,
      String artistName,
      Long artistUserId,
      String artworkImageUrl,
      int imageWidth,
      int imageHeight,
      LocalDateTime createdAt,
      ExhibitionInfoResponse exhibitionInfo,
      @Schema(
              description = "대표 사진 표시 방식. FILL: 꽉 채우기, FIT: 전체 보이기",
              allowableValues = {"FILL", "FIT"})
          ArtworkFit artworkFit) {}

  public record ExhibitionInfoResponse(
      Long displayId, String exhibitionTitle, String exhibitionPeriod, String exhibitionLocation) {}
}
