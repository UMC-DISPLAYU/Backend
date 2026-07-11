package com.example.demo.global.error;

public class BusinessException extends RuntimeException {

  private final BaseErrorCode errorCode;

  public BusinessException(BaseErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public BusinessException(BaseErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public BusinessException(BaseErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }

  public BaseErrorCode errorCode() {
    return errorCode;
  }
}
