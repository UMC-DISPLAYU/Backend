package com.example.demo.domain.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(

        @Schema(
                description = "로그아웃할 Refresh Token",
                example = "eyJhbGciOi..."
        )
        @NotBlank
        String refreshToken
) {}
