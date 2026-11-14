package com.example.demo.domain.user.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record OAuthAuthorizationUrlResponse(
    @Schema(
            description = "소셜 로그인 인가 페이지 URL",
            example =
                "https://kauth.kakao.com/oauth/authorize?client_id=displayu&state=550e8400-e29b-41d4-a716-446655440000")
        String authorizationUrl) {}
