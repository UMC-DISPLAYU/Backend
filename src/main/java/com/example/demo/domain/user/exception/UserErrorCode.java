package com.example.demo.domain.user.exception;

import com.example.demo.global.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
  DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "DUPLICATE_NICKNAME", "이미 사용 중인 닉네임입니다."),
  AGREEMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "AGREEMENT_NOT_FOUND", "존재하지 않는 약관입니다."),

  INVALID_NICKNAME_FORMAT(
      HttpStatus.BAD_REQUEST,
      "INVALID_NICKNAME_FORMAT",
      "닉네임은 한글, 영문, 숫자로 5~15자여야 하며 공백과 특수문자는 사용할 수 없습니다."),

  REQUIRED_AGREEMENT_NOT_ACCEPTED(
      HttpStatus.BAD_REQUEST, "REQUIRED_AGREEMENT_NOT_ACCEPTED", "필수 약관에 동의해야 회원가입할 수 있습니다."),

  ALREADY_REGISTERED_USER(HttpStatus.CONFLICT, "ALREADY_REGISTERED_USER", "이미 가입된 사용자입니다."),

  MISSING_NICKNAME(HttpStatus.BAD_REQUEST, "MISSING_NICKNAME", "닉네임을 입력해주세요.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
