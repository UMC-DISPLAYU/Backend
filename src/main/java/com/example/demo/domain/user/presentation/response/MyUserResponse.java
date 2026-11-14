package com.example.demo.domain.user.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MyUserResponse(
    @Schema(example = "1") Long id,
    @Schema(example = "Kakao") String provider,
    @Schema(example = "김디유") String name,
    @Schema(example = "displayu01") String nickname,
    @Schema(example = "https://cdn.displayu.co.kr/users/1/profile.jpg", nullable = true)
        String profileImageUrl,
    @Schema(example = "USER") String role,
    @Schema(example = "true") boolean isVerified,
    @Schema(example = "diyu@gmail.com") String socialEmail,
    @Schema(example = "diyu@dongguk.edu", nullable = true) String schoolEmail) {}
