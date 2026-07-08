package com.example.demo.global.error;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

  HttpStatus getStatus();

  String getCode();

  String getMessage();
}
