package com.example.demo.domain.displayartwork.presentation.response;

import com.example.demo.domain.displayartwork.domain.type.ArtworkFit;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record DisplayArtworkListResponse(List<ArtworkItemResponse> artworks) {

  public record ArtworkItemResponse(
      Long artworkId,
      String artworkName,
      String artistName,
      Long artistUserId,
      String artworkImageUrl,
      int imageWidth,
      int imageHeight,
      String artworkVideoUrl,
      List<Long> artistUserIds,
      @Schema(description = "대표 작가가 첫 번째이며 공동 작가가 이어집니다. Q&A 전용 담당자는 제외합니다.")
          List<DisplayArtworkPreviewResponse.ArtistResponse> artists,
      ArtworkCropResponse artworkCrop,
      @Schema(
              description = "대표 사진 표시 방식. FILL: 꽉 채우기, FIT: 전체 보이기",
              allowableValues = {"FILL", "FIT"})
          ArtworkFit artworkFit) {}
}
