package com.example.demo.domain.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResendSchoolEmailVerificationRequest(
    @Schema(description = "재발송할 학교 이메일", example = "user@university.ac.kr") String schoolEmail) {}
