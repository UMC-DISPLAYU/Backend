package com.example.demo.domain.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SchoolEmailVerificationConfirmRequest(
    @Schema(description = "인증할 학교 이메일", example = "user@university.ac.kr") @NotBlank String schoolEmail,
    @Schema(description = "사용자가 입력한 인증번호", example = "123456") @NotBlank String verificationCode) {}
