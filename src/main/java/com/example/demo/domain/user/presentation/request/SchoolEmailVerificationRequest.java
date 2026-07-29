package com.example.demo.domain.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SchoolEmailVerificationRequest(
    @Schema(description = "인증할 학교 이메일", example = "user@duksung.ac.kr") String schoolEmail,
    @Schema(description = "GET /api/v1/schools 검색 결과에서 선택한 대학교 표시명", example = "덕성여자대학교")
        String univName) {}
