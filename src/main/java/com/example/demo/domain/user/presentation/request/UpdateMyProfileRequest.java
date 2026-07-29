package com.example.demo.domain.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateMyProfileRequest(
    @Schema(example = "https://cdn.example.com/images/user/profile.jpg") String profileImageUrl,
    @Schema(description = "한글, 영문, 숫자만 사용한 5~15자 닉네임", example = "거장마야01") String nickname) {}
