package com.example.demo.domain.user.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record NicknameCheckResponse(
    @Schema(description = "확인한 닉네임", example = "maya041225") String nickname,
    @Schema(description = "닉네임 사용 가능 여부", example = "true") boolean isAvailable) {}
