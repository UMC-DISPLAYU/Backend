package com.example.demo.domain.archive.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum ArchiveErrorCode implements BaseErrorCode {
  ALREADY_ARCHIVED_DISPLAY(HttpStatus.CONFLICT, "ALREADY_ARCHIVED_DISPLAY", "이미 저장한 전시입니다."),
  ARCHIVE_DISPLAY_NOT_FOUND(
      HttpStatus.NOT_FOUND, "ARCHIVE_DISPLAY_NOT_FOUND", "저장된 전시를 찾을 수 없습니다."),
  ALREADY_ARCHIVED_WORK(HttpStatus.CONFLICT, "ALREADY_ARCHIVED_WORK", "이미 저장한 작품입니다."),
  ARCHIVE_WORK_NOT_FOUND(HttpStatus.NOT_FOUND, "ARCHIVE_WORK_NOT_FOUND", "저장된 작품을 찾을 수 없습니다."),
  ALREADY_ARCHIVED_ARTIST(HttpStatus.CONFLICT, "ALREADY_ARCHIVED_ARTIST", "이미 저장한 작가입니다."),
  ARCHIVE_ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "ARCHIVE_ARTIST_NOT_FOUND", "저장된 작가를 찾을 수 없습니다."),
  ARTIST_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTIST_PROFILE_NOT_FOUND", "존재하지 않는 작가 프로필입니다."),
  DISPLAY_NOT_FOUND(HttpStatus.NOT_FOUND, "DISPLAY_NOT_FOUND", "존재하지 않는 전시입니다."),
  DISPLAY_ARTWORK_NOT_FOUND(HttpStatus.NOT_FOUND, "DISPLAY_ARTWORK_NOT_FOUND", "존재하지 않는 작품입니다."),
  ALREADY_ARCHIVED_PERSONAL_WORK(
      HttpStatus.CONFLICT, "ALREADY_ARCHIVED_PERSONAL_WORK", "이미 저장한 개인 작품입니다."),
  ARCHIVE_PERSONAL_WORK_NOT_FOUND(
      HttpStatus.NOT_FOUND, "ARCHIVE_PERSONAL_WORK_NOT_FOUND", "저장된 개인 작품을 찾을 수 없습니다."),
  PERSONAL_ARTWORK_NOT_FOUND(
      HttpStatus.NOT_FOUND, "PERSONAL_ARTWORK_NOT_FOUND", "존재하지 않는 개인 작품입니다.");

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
