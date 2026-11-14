package com.example.demo.domain.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserSearchRequest(
    @Schema(description = "정확히 일치하는 사용자를 찾을 닉네임", example = "quietroom") @NotBlank String nickname) {

  public String normalizedNickname() {
    return nickname.trim();
  }
}
