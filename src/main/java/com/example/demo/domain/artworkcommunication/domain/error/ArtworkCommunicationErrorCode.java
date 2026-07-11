package com.example.demo.domain.artworkcommunication.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum ArtworkCommunicationErrorCode implements BaseErrorCode {
  ARTWORK_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTWORK_NOT_FOUND", "작품을 찾을 수 없습니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
  FEELING_NOT_FOUND(HttpStatus.NOT_FOUND, "FEELING_NOT_FOUND", "감상평을 찾을 수 없습니다."),
  ARTWORK_FEELING_FORBIDDEN(
      HttpStatus.FORBIDDEN, "ARTWORK_FEELING_FORBIDDEN", "감상평에 대한 권한이 없습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

  ArtworkCommunicationErrorCode(HttpStatus status, String code, String message) {
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
