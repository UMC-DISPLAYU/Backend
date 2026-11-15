package com.example.demo.domain.displayartwork.presentation.response;

import com.example.demo.domain.displayartwork.domain.vo.ArtworkCrop;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(types = {"object", "null"})
public record ArtworkCropResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal x,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal y,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal w,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal h) {
  public static ArtworkCropResponse from(ArtworkCrop crop) {
    return crop == null
        ? null
        : new ArtworkCropResponse(crop.getX(), crop.getY(), crop.getW(), crop.getH());
  }
}
