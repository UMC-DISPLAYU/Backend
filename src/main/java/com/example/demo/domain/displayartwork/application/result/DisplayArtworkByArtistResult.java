package com.example.demo.domain.displayartwork.application.result;

import com.example.demo.domain.displayartwork.domain.type.ArtworkFit;
import java.time.LocalDateTime;
import java.util.List;

public record DisplayArtworkByArtistResult(List<ArtworkCardResult> artworks) {

  public record ArtworkCardResult(
      Long artworkId,
      String artworkName,
      String artistName,
      Long artistUserId,
      String artworkImageUrl,
      int imageWidth,
      int imageHeight,
      LocalDateTime createdAt,
      ExhibitionInfoResult exhibitionInfo,
      ArtworkFit artworkFit) {}

  public record ExhibitionInfoResult(
      Long displayId, String exhibitionTitle, String exhibitionPeriod, String exhibitionLocation) {}
}
