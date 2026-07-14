package com.example.demo.domain.displayartwork.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum DisplayArtworkErrorCode implements BaseErrorCode {
  DISPLAY_ARTWORK_NOT_FOUND(
      HttpStatus.NOT_FOUND, "DISPLAY_ARTWORK_NOT_FOUND", "전시 출품작을 찾을 수 없습니다."),
  AT_LEAST_ONE_ARTWORK_IMAGE_REQUIRED(
      HttpStatus.BAD_REQUEST,
      "AT_LEAST_ONE_ARTWORK_IMAGE_REQUIRED",
      "작품 이미지(ARTWORK 타입)가 최소 1장 필요합니다."),
  DISPLAY_NOT_FOUND(HttpStatus.NOT_FOUND, "DISPLAY_NOT_FOUND", "전시를 찾을 수 없습니다."),
  NOT_DISPLAY_TEAM_MEMBER(
      HttpStatus.FORBIDDEN, "NOT_DISPLAY_TEAM_MEMBER", "해당 전시의 팀원만 작품을 등록할 수 있습니다."),
  ARTWORK_REGISTRATION_LIMIT_EXCEEDED(
      HttpStatus.BAD_REQUEST, "ARTWORK_REGISTRATION_LIMIT_EXCEEDED", "전시당 등록 가능한 작품 개수를 초과했습니다."),
  FORBIDDEN_ARTWORK_ACTION(HttpStatus.FORBIDDEN, "FORBIDDEN_ARTWORK_ACTION", "해당 작품에 대한 권한이 없습니다."),
  INVALID_ARTWORK_ORDER_LIST(
      HttpStatus.BAD_REQUEST,
      "INVALID_ARTWORK_ORDER_LIST",
      "전시에 등록된 작품 목록과 순서 변경 요청 목록이 일치하지 않습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

  DisplayArtworkErrorCode(HttpStatus status, String code, String message) {
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
