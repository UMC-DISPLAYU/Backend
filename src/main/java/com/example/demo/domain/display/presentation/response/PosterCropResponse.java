package com.example.demo.domain.display.presentation.response;

import com.example.demo.domain.display.domain.vo.PosterCrop;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(types = {"object", "null"})
public record PosterCropResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal x,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal y,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal w,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal h) {
  public static PosterCropResponse from(PosterCrop crop) {
    return crop == null
        ? null
        : new PosterCropResponse(crop.getX(), crop.getY(), crop.getW(), crop.getH());
  }
}
