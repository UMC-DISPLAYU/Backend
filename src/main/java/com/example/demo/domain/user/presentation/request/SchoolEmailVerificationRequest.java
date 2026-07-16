package com.example.demo.domain.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SchoolEmailVerificationRequest(
    @Schema(description = "인증할 학교 이메일", example = "user@university.ac.kr") String schoolEmail,
    @Schema(description = "학교명", example = "덕성여자대학교") String univName) {}
