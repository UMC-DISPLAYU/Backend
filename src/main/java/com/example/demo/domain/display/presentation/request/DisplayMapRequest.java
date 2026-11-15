package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.application.query.DisplayMapQuery;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
    @Schema(description = "페이징 개수. 기본20, 1~100. all=true이면 무시합니다.") Integer size,
    @Schema(description = "true이면 현재 좌표 범위의 예정·진행 전시 전체를 반환합니다. cursor와 함께 사용할 수 없습니다.")
        Boolean all) {

  private static final int DEFAULT_SIZE = 20;

  public DisplayMapQuery toQuery() {
    validateBounds();
    if (Boolean.TRUE.equals(all) && cursor != null) {
      throw new BusinessException(
          GlobalErrorCode.INVALID_INPUT_VALUE, "전체 조회에는 cursor를 전달할 수 없습니다.");
    }
    if (!Boolean.TRUE.equals(all) && size != null && (size < 1 || size > 100)) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE, "size는 1 이상 100 이하이어야 합니다.");
    }
    return new DisplayMapQuery(
        southLatitude,
        westLongitude,
        northLatitude,
        eastLongitude,
        normalizeSearchWord(),
        cursor,
        requestedSize(),
        Boolean.TRUE.equals(all));
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
    return Boolean.TRUE.equals(all) || size == null ? DEFAULT_SIZE : size;
  }
}
