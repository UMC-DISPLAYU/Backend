package com.example.demo.global.error;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

    HttpStatus status();
    String code();
    String message();
}
