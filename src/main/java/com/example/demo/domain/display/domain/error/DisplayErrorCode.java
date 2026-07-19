package com.example.demo.domain.display.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum DisplayErrorCode implements BaseErrorCode {
  DISPLAY_NOT_FOUND(HttpStatus.NOT_FOUND, "DISPLAY_NOT_FOUND", "전시를 찾을 수 없습니다."),
  DISPLAY_INVITATION_NOT_ISSUED(
      HttpStatus.BAD_REQUEST, "DISPLAY_INVITATION_NOT_ISSUED", "초대 링크가 아직 발급되지 않았습니다."),
  INVALID_DISPLAY_INVITATION_TOKEN(
      HttpStatus.NOT_FOUND, "INVALID_DISPLAY_INVITATION_TOKEN", "유효하지 않은 초대 링크입니다."),
  DISPLAY_INVITATION_DISABLED(HttpStatus.GONE, "DISPLAY_INVITATION_DISABLED", "비활성화된 초대 링크입니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

  DisplayErrorCode(HttpStatus status, String code, String message) {
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
