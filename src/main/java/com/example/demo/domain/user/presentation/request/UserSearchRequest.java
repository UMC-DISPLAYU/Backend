package com.example.demo.domain.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserSearchRequest(
    @Schema(description = "검색할 닉네임의 전체 또는 일부", example = "quiet") @NotBlank String nickname) {

  public String normalizedNickname() {
    return nickname.trim();
  }
}
