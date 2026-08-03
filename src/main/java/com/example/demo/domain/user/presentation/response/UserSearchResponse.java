package com.example.demo.domain.user.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserSearchResponse(
    @Schema(description = "사용자 ID", example = "12") Long userId,
    @Schema(description = "사용자 이름", example = "이정우") String name,
    @Schema(description = "사용자 닉네임", example = "quietroom") String nickname) {}
