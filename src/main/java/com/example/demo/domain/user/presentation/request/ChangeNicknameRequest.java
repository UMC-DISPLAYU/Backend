package com.example.demo.domain.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeNicknameRequest(
    @Schema(description = "변경할 닉네임", example = "User2") String nickname) {}
