package com.example.demo.domain.personalartworkcommunication.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum PersonalArtworkCommunicationErrorCode implements BaseErrorCode {
  CREATOR_CANNOT_WRITE_FEELING(
      HttpStatus.FORBIDDEN, "CREATOR_CANNOT_WRITE_FEELING", "작업자는 본인 작품에 감상평을 작성할 수 없습니다."),
  CREATOR_CANNOT_WRITE_QUESTION(
      HttpStatus.FORBIDDEN, "CREATOR_CANNOT_WRITE_QUESTION", "작업자는 본인 작품에 질문을 작성할 수 없습니다."),
  INVALID_FEELING_CONTENT(HttpStatus.BAD_REQUEST, "INVALID_FEELING_CONTENT", "감상평 내용을 입력해주세요."),
  INVALID_QUESTION_CONTENT(HttpStatus.BAD_REQUEST, "INVALID_QUESTION_CONTENT", "질문 내용을 입력해주세요."),
  PERSONAL_ARTWORK_FEELING_FORBIDDEN(
      HttpStatus.FORBIDDEN, "PERSONAL_ARTWORK_FEELING_FORBIDDEN", "감상평에 대한 권한이 없습니다."),
  PERSONAL_ARTWORK_NOT_FOUND(HttpStatus.NOT_FOUND, "PERSONAL_NOT_FOUND", "개인 작품을 찾을 수 없습니다."),
  PERSONAL_ARTWORK_QUESTION_FORBIDDEN(
      HttpStatus.FORBIDDEN, "PERSONAL_ARTWORK_QUESTION_FORBIDDEN", "질문에 대한 권한이 없습니다."),
  PERSONAL_FEELING_NOT_FOUND(
      HttpStatus.NOT_FOUND, "PERSONAL_FEELING_NOT_FOUND", "개인 작품 감상평을 찾을 수 없습니다."),
  PERSONAL_QUESTION_NOT_FOUND(
      HttpStatus.NOT_FOUND, "PERSONAL_QUESTION_NOT_FOUND", "개인 작품 질문을 찾을 수 없습니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
  ;

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
