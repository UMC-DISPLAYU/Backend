package com.example.demo.domain.personalartwork.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum PersonalArtworkErrorCode implements BaseErrorCode {
  PERSONAL_ARTWORK_NOT_FOUND(
      HttpStatus.NOT_FOUND, "PERSONAL_ARTWORK_NOT_FOUND", "개인 작품을 찾을 수 없습니다."),
  AT_LEAST_ONE_ARTWORK_IMAGE_REQUIRED(
      HttpStatus.BAD_REQUEST,
      "AT_LEAST_ONE_ARTWORK_IMAGE_REQUIRED",
      "작품 이미지(ARTWORK 타입)가 최소 1장 필요합니다.");

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
