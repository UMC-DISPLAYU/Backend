package com.example.demo.domain.lounge.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum LoungeErrorCode implements BaseErrorCode {
  LOUNGE_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "LOUNGE_POST_NOT_FOUND", "라운지 게시글을 찾을 수 없습니다."),
  LOUNGE_POST_LIKE_NOT_FOUND(
      HttpStatus.NOT_FOUND, "LOUNGE_POST_LIKE_NOT_FOUND", "라운지 게시글 좋아요를 찾을 수 없습니다."),
  LOUNGE_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "LOUNGE_COMMENT_NOT_FOUND", "라운지 댓글을 찾을 수 없습니다."),
  LOUNGE_COMMENT_LIKE_NOT_FOUND(
      HttpStatus.NOT_FOUND, "LOUNGE_COMMENT_LIKE_NOT_FOUND", "라운지 댓글 좋아요를 찾을 수 없습니다."),
  LOUNGE_ARTIST_VERIFICATION_REQUIRED(
      HttpStatus.FORBIDDEN, "LOUNGE_ARTIST_VERIFICATION_REQUIRED", "작가 인증이 필요합니다."),
  LOUNGE_POST_CONCURRENT_WRITE_CONFLICT(
      HttpStatus.CONFLICT,
      "LOUNGE_POST_CONCURRENT_WRITE_CONFLICT",
      "라운지 게시글 변경 중 충돌이 발생했습니다. 다시 시도해주세요."),
  INVALID_REPLY_TARGET(HttpStatus.BAD_REQUEST, "INVALID_REPLY_TARGET", "유효하지 않은 답글 대상입니다.");
  private final HttpStatus status;
  private final String code;
  private final String message;

  LoungeErrorCode(HttpStatus status, String code, String message) {
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
