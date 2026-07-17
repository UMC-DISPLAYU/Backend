package com.example.demo.domain.user.presentation.response;

public record MyUserResponse(
    Long id,
    String provider,
    String name,
    String nickname,
    boolean isVerified,
    String socialEmail,
    String schoolEmail) {}
