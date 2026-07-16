package com.example.demo.domain.user.exception;

import com.example.demo.global.error.BaseErrorCode;
import com.example.demo.global.error.BusinessException;
import lombok.Getter;

@Getter
public class UserException extends BusinessException {

  public UserException(BaseErrorCode errorCode) {
    super(errorCode);
  }
}
