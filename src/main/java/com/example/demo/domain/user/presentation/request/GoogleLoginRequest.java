package com.example.demo.domain.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
    @Schema(description = "Google Identity에서 발급받은 ID Token", example = "eyJhbGciOi...")
        @NotBlank(message = "Google ID Token은 필수입니다.") String idToken) {}
