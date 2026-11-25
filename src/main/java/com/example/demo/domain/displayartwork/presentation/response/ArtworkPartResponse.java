package com.example.demo.domain.displayartwork.presentation.response;

import com.example.demo.domain.displayartwork.domain.vo.ArtworkPart;
import io.swagger.v3.oas.annotations.media.Schema;

public record ArtworkPartResponse(
    @Schema(example = "Blue Moment II") String artworkName,
    @Schema(example = "2026") Integer productionYear,
    @Schema(example = "Oil on canvas") String materialMedia,
    @Schema(example = "45 x 60 cm") String size) {
  public static ArtworkPartResponse from(ArtworkPart part) {
    return new ArtworkPartResponse(
        part.artworkName(), part.productionYear(), part.materialMedia(), part.size());
  }
}
