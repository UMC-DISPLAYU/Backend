package com.example.demo.domain.artist.exception;

import com.example.demo.global.error.BaseErrorCode;
import com.example.demo.global.error.BusinessException;

public class ArtistException extends BusinessException {

  public ArtistException(BaseErrorCode errorCode) {
    super(errorCode);
  }
}
