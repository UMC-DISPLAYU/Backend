package com.example.demo.domain.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record RefreshRequest(

        @Schema(
                description = "재발급에 사용할 Refresh Token",
                example = "eyJhbGciOi..."
        )
        String refreshToken
) {}
