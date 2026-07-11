package com.example.demo.domain.user.exception;

import com.example.demo.global.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {


    INVALID_SOCIAL_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "INVALID_SOCIAL_TOKEN",
            "유효하지 않은 소셜 토큰입니다."
    ),


    INVALID_SIGNUP_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "INVALID_SIGNUP_TOKEN",
            "유효하지 않은 회원가입 토큰입니다."
    ),


    INVALID_REFRESH_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "INVALID_REFRESH_TOKEN",
            "유효하지 않은 리프레시 토큰입니다."
    ),


    EXPIRED_REFRESH_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "EXPIRED_REFRESH_TOKEN",
            "만료된 리프레시 토큰입니다."
    ),


    REFRESH_TOKEN_NOT_FOUND(
            HttpStatus.UNAUTHORIZED,
            "REFRESH_TOKEN_NOT_FOUND",
            "저장된 리프레시 토큰을 찾을 수 없습니다."
    ),


    WITHDRAWAL_USER(
            HttpStatus.FORBIDDEN,
            "WITHDRAWAL_USER",
            "탈퇴한 사용자입니다."
    );


    private final HttpStatus status;
    private final String code;
    private final String message;
}
