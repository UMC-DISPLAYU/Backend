package com.example.demo.domain.user.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SchoolSearchResponse(
    @Schema(description = "대학교 표시명", example = "서울대학교") String name) {}
