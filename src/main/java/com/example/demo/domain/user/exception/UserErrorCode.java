package com.example.demo.domain.user.exception;

import com.example.demo.global.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

    DUPLICATE_NICKNAME(
            HttpStatus.CONFLICT,
            "DUPLICATE_NICKNAME",
            "이미 사용 중인 닉네임입니다."),

    AGREEMENT_NOT_FOUND(
            HttpStatus.BAD_REQUEST,
            "AGREEMENT_NOT_FOUND",
            "존재하지 않는 약관입니다."),

    INVALID_NICKNAME_FORMAT(
            HttpStatus.BAD_REQUEST,
            "INVALID_NICKNAME_FORMAT",
            "닉네임은 한글, 영문, 숫자로 2~15자여야 하며 공백과 특수문자는 사용할 수 없습니다."),

    REQUIRED_AGREEMENT_NOT_ACCEPTED(
            HttpStatus.BAD_REQUEST,
            "REQUIRED_AGREEMENT_NOT_ACCEPTED",
            "필수 약관에 동의해야 회원가입할 수 있습니다."),

    ALREADY_REGISTERED_USER(
            HttpStatus.CONFLICT,
            "ALREADY_REGISTERED_USER",
            "이미 가입된 사용자입니다."),

    MISSING_NICKNAME(
            HttpStatus.BAD_REQUEST,
            "MISSING_NICKNAME",
            "닉네임을 입력해주세요."),

    UNSUPPORTED_UNIVERSITY(
            HttpStatus.BAD_REQUEST,
            "UNSUPPORTED_UNIVERSITY",
            "지원하지 않는 대학교입니다."),

    INVALID_EMAIL(
            HttpStatus.BAD_REQUEST,
            "INVALID_EMAIL",
            "유효하지 않은 학교 이메일입니다."),

    ALREADY_VERIFIED_USER(
            HttpStatus.BAD_REQUEST,
            "ALREADY_VERIFIED_USER",
            "이미 작가 인증이 완료된 사용자입니다."),

    EMAIL_SEND_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "EMAIL_SEND_FAILED",
            "인증 이메일 발송에 실패했습니다."),

    TOO_MANY_REQUESTS(
            HttpStatus.TOO_MANY_REQUESTS,
            "TOO_MANY_REQUESTS",
            "인증번호 요청 횟수를 초과했습니다."),

    EMAIL_VERIFICATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "EMAIL_VERIFICATION_NOT_FOUND",
            "발급된 인증번호를 찾을 수 없습니다."),

    EMAIL_SEND_COOLDOWN(
            HttpStatus.TOO_MANY_REQUESTS,
            "EMAIL_SEND_COOLDOWN",
            "인증 이메일은 잠시 후 다시 요청할 수 있습니다."),

    DUPLICATE_SCHOOL_EMAIL(
            HttpStatus.CONFLICT,
            "DUPLICATE_SCHOOL_EMAIL",
            "이미 다른 계정에서 작가 인증에 사용된 학교 이메일입니다."),

    VERIFICATION_CODE_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "VERIFICATION_CODE_MISMATCH",
            "인증번호가 일치하지 않습니다."),

    VERIFICATION_CODE_EXPIRED(
            HttpStatus.BAD_REQUEST,
            "VERIFICATION_CODE_EXPIRED",
            "인증번호가 만료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}