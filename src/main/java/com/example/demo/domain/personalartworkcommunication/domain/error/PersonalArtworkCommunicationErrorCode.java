package com.example.demo.domain.personalartworkcommunication.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum PersonalArtworkCommunicationErrorCode implements BaseErrorCode {
  PERSONAL_ARTWORK_NOT_FOUND(HttpStatus.NOT_FOUND, "PERSONAL_NOT_FOUND", "개인 작품을 찾을 수 없습니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
  INVALID_FEELING_CONTENT(HttpStatus.BAD_REQUEST, "INAVLID_FEELING_CONTENT", "감상평 내용을 입력해주세요."),
  CREATOR_CANNOT_WRITE_FEELING(
      HttpStatus.FORBIDDEN, "CREATOR_CANNOT_WRITE_FEELING", "작업자는 본인 작품에 감상평을 작성할 수 없습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

  PersonalArtworkCommunicationErrorCode(HttpStatus status, String code, String message) {
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
