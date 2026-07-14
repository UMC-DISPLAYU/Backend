package com.example.demo.domain.user.exception;

import com.example.demo.global.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
  DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "DUPLICATE_NICKNAME", "이미 사용 중인 닉네임입니다."),

  REQUIRED_AGREEMENT_NOT_ACCEPTED(
      HttpStatus.BAD_REQUEST, "REQUIRED_AGREEMENT_NOT_ACCEPTED", "필수 약관에 동의해야 회원가입할 수 있습니다."),

  ALREADY_REGISTERED_USER(HttpStatus.CONFLICT, "ALREADY_REGISTERED_USER", "이미 가입된 사용자입니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
