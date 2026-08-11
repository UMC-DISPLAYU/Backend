package com.example.demo.domain.artworkcommunication.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum ArtworkCommunicationErrorCode implements BaseErrorCode {
  ARTWORK_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTWORK_NOT_FOUND", "작품을 찾을 수 없습니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
  FEELING_NOT_FOUND(HttpStatus.NOT_FOUND, "FEELING_NOT_FOUND", "감상평을 찾을 수 없습니다."),
  FEELING_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "FEELING_REPLY_NOT_FOUND", "감상평 답변을 찾을 수 없습니다."),
  FEELING_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "FEELING_LIKE_NOT_FOUND", "감상평 좋아요를 찾을 수 없습니다."),
  FEELING_REPLY_LIKE_NOT_FOUND(
      HttpStatus.NOT_FOUND, "FEELING_REPLY_LIKE_NOT_FOUND", "감상평 답변 좋아요를 찾을 수 없습니다."),
  QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QUESTION_NOT_FOUND", "질문을 찾을 수 없습니다."),
  QUESTION_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "QUESTION_REPLY_NOT_FOUND", "질문 답변을 찾을 수 없습니다."),
  INVALID_FEELING_CONTENT(HttpStatus.BAD_REQUEST, "INVALID_FEELING_CONTENT", "감상평 내용을 입력해주세요."),
  INVALID_FEELING_IMAGES(
      HttpStatus.BAD_REQUEST, "INVALID_FEELING_IMAGES", "감상평 이미지는 최대 5개의 유효한 이미지여야 합니다."),
  INVALID_QUESTION_CONTENT(HttpStatus.BAD_REQUEST, "INVALID_QUESTION_CONTENT", "질문 내용을 입력해주세요."),
  INVALID_QUESTION_IMAGES(
      HttpStatus.BAD_REQUEST, "INVALID_QUESTION_IMAGES", "질문 이미지는 최대 5개의 유효한 이미지여야 합니다."),
  INVALID_QUESTION_REPLY_IMAGES(
      HttpStatus.BAD_REQUEST, "INVALID_QUESTION_REPLY_IMAGES", "질문 답변 이미지는 최대 5개의 유효한 이미지여야 합니다."),
  CREATOR_CANNOT_WRITE_FEELING(
      HttpStatus.FORBIDDEN, "CREATOR_CANNOT_WRITE_FEELING", "작업자는 본인 작품에 감상평을 작성할 수 없습니다."),
  ARTWORK_FEELING_FORBIDDEN(HttpStatus.FORBIDDEN, "ARTWORK_FEELING_FORBIDDEN", "감상평에 대한 권한이 없습니다."),
  ARTWORK_FEELING_REPLY_FORBIDDEN(
      HttpStatus.FORBIDDEN, "ARTWORK_FEELING_REPLY_FORBIDDEN", "감상평 답변에 대한 권한이 없습니다."),
  ARTWORK_QUESTION_FORBIDDEN(
      HttpStatus.FORBIDDEN, "ARTWORK_QUESTION_FORBIDDEN", "질문에 대한 권한이 없습니다."),
  ARTWORK_QUESTION_REPLY_FORBIDDEN(
      HttpStatus.FORBIDDEN, "ARTWORK_QUESTION_REPLY_FORBIDDEN", "질문 답변에 대한 권한이 없습니다."),
  QNA_CONTACT_FORBIDDEN(HttpStatus.FORBIDDEN, "QNA_CONTACT_FORBIDDEN", "QnA 담당 작업자만 답변할 수 있습니다."),
  QUESTION_ALREADY_ANSWERED(
      HttpStatus.BAD_REQUEST, "QUESTION_ALREADY_ANSWERED", "이미 답변 완료된 질문입니다.");

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
