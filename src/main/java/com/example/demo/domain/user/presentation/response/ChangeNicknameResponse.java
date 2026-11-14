package com.example.demo.domain.user.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeNicknameResponse(
    @Schema(description = "변경된 닉네임", example = "김디유") String nickname) {}
