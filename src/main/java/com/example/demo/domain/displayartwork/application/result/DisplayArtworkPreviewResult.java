package com.example.demo.domain.displayartwork.application.result;

import com.example.demo.domain.displayartwork.domain.type.ArtworkFit;
import com.example.demo.domain.displayartwork.domain.vo.ArtworkCrop;
import java.util.List;

public record DisplayArtworkPreviewResult(
    List<ArtworkCardResult> artworks, int page, int size, boolean isLast) {

  public record ArtworkCardResult(
      Long artworkId,
      String artworkName,
      String artistName,
      Long artistUserId,
      String artworkImageUrl,
      int imageWidth,
      int imageHeight,
      ExhibitionInfoResult exhibitionInfo,
      List<ArtistResult> artists,
      ArtworkCrop artworkCrop,
      ArtworkFit artworkFit) {}

  public record ArtistResult(Long userId, String name) {}

  public record ExhibitionInfoResult(
      Long displayId, String exhibitionTitle, String exhibitionPeriod, String exhibitionLocation) {}
}
