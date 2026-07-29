package com.example.demo.domain.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeNicknameRequest(
    @Schema(description = "변경할 닉네임. 한글, 영문, 숫자만 사용하여 5~15자로 입력합니다.", example = "User02")
        String nickname) {}
