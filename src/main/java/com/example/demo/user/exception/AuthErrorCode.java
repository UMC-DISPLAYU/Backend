package com.example.demo.user.exception;

import com.example.demo.global.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {
  INVALID_SIGNUP_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_SIGNUP_TOKEN", "유효하지 않은 회원가입 토큰입니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
