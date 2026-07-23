package com.example.demo.domain.display.domain.error;

import com.example.demo.global.error.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum DisplayErrorCode implements BaseErrorCode {
  DISPLAY_NOT_FOUND(HttpStatus.NOT_FOUND, "DISPLAY_NOT_FOUND", "전시를 찾을 수 없습니다."),
  DISPLAY_INVITATION_NOT_ISSUED(
      HttpStatus.BAD_REQUEST, "DISPLAY_INVITATION_NOT_ISSUED", "초대 링크가 아직 발급되지 않았습니다."),
  INVALID_DISPLAY_INVITATION_TOKEN(
      HttpStatus.NOT_FOUND, "INVALID_DISPLAY_INVITATION_TOKEN", "유효하지 않은 초대 링크입니다."),
  DISPLAY_INVITATION_DISABLED(HttpStatus.GONE, "DISPLAY_INVITATION_DISABLED", "비활성화된 초대 링크입니다."),
  DISPLAY_INVITATION_PERMISSION_DENIED(
      HttpStatus.FORBIDDEN, "DISPLAY_INVITATION_PERMISSION_DENIED", "전시 멤버를 초대할 권한이 없습니다."),
  SELF_INVITATION_NOT_ALLOWED(
      HttpStatus.BAD_REQUEST, "SELF_INVITATION_NOT_ALLOWED", "자기 자신은 초대할 수 없습니다."),
  ALREADY_DISPLAY_MEMBER(HttpStatus.CONFLICT, "ALREADY_DISPLAY_MEMBER", "이미 전시 멤버인 사용자입니다."),
  PENDING_DISPLAY_INVITATION_EXISTS(
      HttpStatus.CONFLICT, "PENDING_DISPLAY_INVITATION_EXISTS", "처리 대기 중인 전시 멤버 초대가 이미 존재합니다."),
  DISPLAY_MEMBER_INVITATION_NOT_FOUND(
      HttpStatus.NOT_FOUND, "DISPLAY_MEMBER_INVITATION_NOT_FOUND", "전시 멤버 초대를 찾을 수 없습니다."),
  DISPLAY_INVITATION_INVITEE_MISMATCH(
      HttpStatus.FORBIDDEN, "DISPLAY_INVITATION_INVITEE_MISMATCH", "해당 초대의 대상 사용자가 아닙니다."),
  DISPLAY_INVITATION_ALREADY_ACCEPTED(
      HttpStatus.CONFLICT, "DISPLAY_INVITATION_ALREADY_ACCEPTED", "이미 수락된 전시 멤버 초대입니다."),
  DISPLAY_INVITATION_ALREADY_REJECTED(
      HttpStatus.CONFLICT, "DISPLAY_INVITATION_ALREADY_REJECTED", "이미 거절된 전시 멤버 초대입니다."),
  INVALID_DISPLAY_INVITATION_STATUS(
      HttpStatus.CONFLICT, "INVALID_DISPLAY_INVITATION_STATUS", "처리할 수 없는 전시 멤버 초대 상태입니다."),
  DISPLAY_MEMBER_CONCURRENTLY_CREATED(
      HttpStatus.CONFLICT, "DISPLAY_MEMBER_CONCURRENTLY_CREATED", "동시 요청으로 전시 멤버가 이미 생성되었습니다.");

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
