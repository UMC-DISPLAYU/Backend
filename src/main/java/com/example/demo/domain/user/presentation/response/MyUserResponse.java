package com.example.demo.domain.user.presentation.response;

public record MyUserResponse(
    Long id,
    String provider,
    String name,
    String nickname,
    String profileImageUrl,
    boolean isVerified,
    String socialEmail,
    String schoolEmail) {}
