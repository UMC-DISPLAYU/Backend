package com.example.demo.domain.displaycommunication.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum DisplayCommunicationErrorCode implements BaseErrorCode {
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
  DISPLAY_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "DISPLAY_REVIEW_NOT_FOUND", "전시 후기를 찾을 수 없습니다."),
  DISPLAY_REVIEW_LIKE_NOT_FOUND(
      HttpStatus.NOT_FOUND, "DISPLAY_REVIEW_LIKE_NOT_FOUND", "전시 후기 좋아요를 찾을 수 없습니다."),
  DISPLAY_REVIEW_REPLY_NOT_FOUND(
      HttpStatus.NOT_FOUND, "DISPLAY_REVIEW_REPLY_NOT_FOUND", "전시 후기 답글을 찾을 수 없습니다."),
  DISPLAY_REVIEW_REPLY_LIKE_NOT_FOUND(
      HttpStatus.NOT_FOUND, "DISPLAY_REVIEW_REPLY_LIKE_NOT_FOUND", "전시 후기 답글 좋아요를 찾을 수 없습니다."),
  INVALID_DISPLAY_REVIEW_CONTENT(
      HttpStatus.BAD_REQUEST, "INVALID_DISPLAY_REVIEW_CONTENT", "후기 내용을 입력해주세요."),
  INVALID_DISPLAY_REVIEW_REPLY_CONTENT(
      HttpStatus.BAD_REQUEST, "INVALID_DISPLAY_REVIEW_REPLY_CONTENT", "후기 답글 내용을 입력해주세요."),
  INVALID_DISPLAY_REVIEW_IMAGES(
      HttpStatus.BAD_REQUEST, "INVALID_DISPLAY_REVIEW_IMAGES", "전시 후기 이미지는 최대 5장까지 등록할 수 있습니다."),
  INVALID_DISPLAY_REVIEW_REPLY_IMAGES(
      HttpStatus.BAD_REQUEST,
      "INVALID_DISPLAY_REVIEW_REPLY_IMAGES",
      "전시 후기 답글 이미지는 최대 5장까지 등록할 수 있습니다."),
  DISPLAY_REVIEW_NOT_WRITABLE(
      HttpStatus.FORBIDDEN, "DISPLAY_REVIEW_NOT_WRITABLE", "시작한 공개 전시에만 후기를 작성할 수 있습니다."),
  DISPLAY_REVIEW_FORBIDDEN(
      HttpStatus.FORBIDDEN, "DISPLAY_REVIEW_FORBIDDEN", "전시 후기 작성자만 삭제할 수 있습니다."),
  DISPLAY_REVIEW_REPLY_FORBIDDEN(
      HttpStatus.FORBIDDEN, "DISPLAY_REVIEW_REPLY_FORBIDDEN", "전시 후기 답글 작성자만 삭제할 수 있습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

  DisplayCommunicationErrorCode(HttpStatus status, String code, String message) {
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
