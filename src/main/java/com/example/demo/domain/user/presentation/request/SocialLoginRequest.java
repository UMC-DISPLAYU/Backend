package com.example.demo.domain.user.presentation.request;

import com.example.demo.domain.user.domain.enums.Provider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SocialLoginRequest(
    @Schema(description = "소셜 로그인 제공자", example = "Kakao") @NotNull(message = "소셜 로그인 제공자는 필수입니다.") Provider provider,
    @Schema(description = "소셜 플랫폼에서 발급받은 ID Token", example = "eyJhbGciOi...")
        @NotBlank(message = "ID Token은 필수입니다.") String idToken) {}
