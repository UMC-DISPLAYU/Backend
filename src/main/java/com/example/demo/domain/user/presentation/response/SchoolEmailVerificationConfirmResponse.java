package com.example.demo.domain.user.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SchoolEmailVerificationConfirmResponse(
    @Schema(description = "인증 완료된 학교 이메일", example = "user@university.ac.kr") String schoolEmail,
    @Schema(description = "작가 인증 완료 여부", example = "true") boolean isVerified) {}
