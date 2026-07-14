package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.application.query.DisplayMapQuery;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record DisplayMapRequest(
    @NotNull @DecimalMin("-90") @DecimalMax("90") BigDecimal southLatitude,
    @NotNull @DecimalMin("-180") @DecimalMax("180") BigDecimal westLongitude,
    @NotNull @DecimalMin("-90") @DecimalMax("90") BigDecimal northLatitude,
    @NotNull @DecimalMin("-180") @DecimalMax("180") BigDecimal eastLongitude,
    String searchWord,
    @Min(1) Long cursor,
    @Min(1) @Max(100) Integer size) {

  private static final int DEFAULT_SIZE = 20;

  public DisplayMapQuery toQuery() {
    validateBounds();
    return new DisplayMapQuery(
        southLatitude,
        westLongitude,
        northLatitude,
        eastLongitude,
        normalizeSearchWord(),
        cursor,
        requestedSize());
  }

  private void validateBounds() {
    if (southLatitude.compareTo(northLatitude) >= 0) {
      throw new BusinessException(
          GlobalErrorCode.INVALID_REQUEST, "southLatitude는 northLatitude보다 작아야 합니다.");
    }
    if (westLongitude.compareTo(eastLongitude) >= 0) {
      throw new BusinessException(
          GlobalErrorCode.INVALID_REQUEST, "westLongitude는 eastLongitude보다 작아야 합니다.");
    }
  }

  private String normalizeSearchWord() {
    return searchWord == null ? null : searchWord.trim();
  }

  private int requestedSize() {
    return size == null ? DEFAULT_SIZE : size;
  }
}
