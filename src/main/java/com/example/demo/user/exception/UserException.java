package com.example.demo.user.exception;

import com.example.demo.global.error.BaseErrorCode;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

  private final BaseErrorCode errorCode;

  public UserException(BaseErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
