package com.example.demo.domain.user.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateMyProfileResponse(
    @Schema(example = "https://cdn.displayu.co.kr/users/1/profile.jpg", nullable = true)
        String profileImageUrl,
    @Schema(example = "김디유") String nickname) {}
