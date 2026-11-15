package com.example.demo.domain.display.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(
    types = {"object", "null"},
    description = "원본 이미지 기준 0~1 비율. 소수점 이하 최대 10자리. x+w와 y+h는 1 이하.")
public record PosterCropRequest(
    @NotNull @DecimalMin("0") @DecimalMax("1") @Digits(integer = 1, fraction = 10) BigDecimal x,
    @NotNull @DecimalMin("0") @DecimalMax("1") @Digits(integer = 1, fraction = 10) BigDecimal y,
    @Schema(description = "소수점 이하 최대 10자리의 양수. 최소 0.0000000001.")
        @NotNull @DecimalMin("0.0000000001") @DecimalMax("1") @Digits(integer = 1, fraction = 10) BigDecimal w,
    @Schema(description = "소수점 이하 최대 10자리의 양수. 최소 0.0000000001.")
        @NotNull @DecimalMin("0.0000000001") @DecimalMax("1") @Digits(integer = 1, fraction = 10) BigDecimal h) {
  @Schema(hidden = true)
  @AssertTrue(message = "posterCrop은 원본 이미지 범위를 벗어날 수 없습니다.") public boolean isContained() {
    return x == null
        || y == null
        || w == null
        || h == null
        || (x.add(w).compareTo(BigDecimal.ONE) <= 0 && y.add(h).compareTo(BigDecimal.ONE) <= 0);
  }
}
