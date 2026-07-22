package com.example.demo.domain.user.application.result;

import com.example.demo.domain.user.domain.enums.Provider;

public record MyUserResult(
    Long id,
    Provider provider,
    String name,
    String nickname,
    String profileImageUrl,
    boolean isVerified,
    String socialEmail,
    String schoolEmail) {}
