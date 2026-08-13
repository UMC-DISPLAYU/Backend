package com.example.demo.domain.personalartwork.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum PersonalArtworkErrorCode implements BaseErrorCode {
  PERSONAL_ARTWORK_NOT_FOUND(
      HttpStatus.NOT_FOUND, "PERSONAL_ARTWORK_NOT_FOUND", "개인 작품을 찾을 수 없습니다."),
  PERSONAL_ARTWORK_LIKE_NOT_FOUND(
      HttpStatus.NOT_FOUND, "PERSONAL_ARTWORK_LIKE_NOT_FOUND", "개인 작품 좋아요를 찾을 수 없습니다."),
  AT_LEAST_ONE_ARTWORK_IMAGE_REQUIRED(
      HttpStatus.BAD_REQUEST,
      "AT_LEAST_ONE_ARTWORK_IMAGE_REQUIRED",
      "작품 이미지(ARTWORK 타입)가 최소 1장 필요합니다."),
  // 전시 출품작 등록과 같은 조건이므로 코드 문자열도 동일하게 두어 프론트가 한 분기로 처리할 수 있게 한다.
  INVALID_ARTWORK_FIELD_COUNT(
      HttpStatus.BAD_REQUEST, "INVALID_ARTWORK_FIELD_COUNT", "작품 분야는 1개 이상 2개 이하로 지정해야 합니다."),
  NOT_VERIFIED_ARTIST(
      HttpStatus.FORBIDDEN, "NOT_VERIFIED_ARTIST", "작가 인증을 완료한 사용자만 개인 작품을 등록할 수 있습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

  PersonalArtworkErrorCode(HttpStatus status, String code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }

  @Override
  public HttpStatus getStatus() {
    return status;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
