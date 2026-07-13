package com.example.demo.domain.user.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record RefreshResponse(

        @Schema(
                description = "새로 발급된 Access Token",
                example = "eyJhbGciOi..."
        )
        String accessToken
) {}
