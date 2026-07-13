package com.example.demo.domain.archive.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum ArchiveErrorCode implements BaseErrorCode {
  ALREADY_ARCHIVED_DISPLAY(HttpStatus.CONFLICT, "ALREADY_ARCHIVED_DISPLAY", "이미 저장한 전시입니다."),
  ARCHIVE_DISPLAY_NOT_FOUND(
      HttpStatus.NOT_FOUND, "ARCHIVE_DISPLAY_NOT_FOUND", "저장된 전시를 찾을 수 없습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

  ArchiveErrorCode(HttpStatus status, String code, String message) {
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
