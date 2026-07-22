package com.example.demo.domain.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateMyProfileRequest(
    @Schema(example = "https://cdn.example.com/images/user/profile.jpg") String profileImageUrl,
    @Schema(example = "거장마야") String nickname) {}
