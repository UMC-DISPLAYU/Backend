package com.example.demo.domain.memo.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum MemoErrorCode implements BaseErrorCode {
  MEMO_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMO_NOT_FOUND", "작성된 메모를 찾을 수 없습니다."),
  MEMO_CONCURRENT_WRITE_CONFLICT(
      HttpStatus.CONFLICT, "MEMO_CONCURRENT_WRITE_CONFLICT", "메모 저장 중 충돌이 발생했습니다. 다시 시도해주세요."),
  ARCHIVE_DISPLAY_NOT_FOUND(
      HttpStatus.NOT_FOUND, "ARCHIVE_DISPLAY_NOT_FOUND", "저장된 전시를 찾을 수 없습니다."),
  ARCHIVE_WORK_NOT_FOUND(HttpStatus.NOT_FOUND, "ARCHIVE_WORK_NOT_FOUND", "저장된 작품을 찾을 수 없습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

  MemoErrorCode(HttpStatus status, String code, String message) {
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
