package com.example.demo.domain.user.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record OAuthAuthorizationUrlResponse(
    @Schema(description = "소셜 로그인 인가 페이지 URL") String authorizationUrl) {}
