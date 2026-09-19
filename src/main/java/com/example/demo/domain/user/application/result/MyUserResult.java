package com.example.demo.domain.user.application.result;

import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.UserRole;

public record MyUserResult(
    Long id,
    Provider provider,
    String name,
    String nickname,
    String profileImageUrl,
    UserRole role,
    boolean isVerified,
    String socialEmail,
    String schoolEmail) {}
