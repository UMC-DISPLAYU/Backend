package com.example.demo.domain.displaycommunication.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum DisplayCommunicationErrorCode implements BaseErrorCode {
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
  INVALID_DISPLAY_REVIEW_CONTENT(
      HttpStatus.BAD_REQUEST, "INVALID_DISPLAY_REVIEW_CONTENT", "후기 내용을 입력해주세요."),
  INVALID_DISPLAY_REVIEW_IMAGES(
      HttpStatus.BAD_REQUEST, "INVALID_DISPLAY_REVIEW_IMAGES", "전시 후기 이미지는 최대 5장까지 등록할 수 있습니다."),
  DISPLAY_REVIEW_NOT_WRITABLE(
      HttpStatus.FORBIDDEN, "DISPLAY_REVIEW_NOT_WRITABLE", "진행 중인 공개 전시에만 후기를 작성할 수 있습니다."),
  DISPLAY_TEAM_MEMBER_REVIEW_FORBIDDEN(
      HttpStatus.FORBIDDEN,
      "DISPLAY_TEAM_MEMBER_REVIEW_FORBIDDEN",
      "전시 팀에 속한 회원은 해당 전시의 후기를 작성할 수 없습니다."),
  DISPLAY_REVIEW_ALREADY_EXISTS(
      HttpStatus.CONFLICT, "DISPLAY_REVIEW_ALREADY_EXISTS", "이미 해당 전시의 후기를 작성했습니다.");

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
