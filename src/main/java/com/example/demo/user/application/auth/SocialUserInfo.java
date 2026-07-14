package com.example.demo.user.application.auth;

import com.example.demo.user.domain.enums.Provider;

public record SocialUserInfo(
        Provider provider,
        String providerId,
        String name,
        String socialEmail
) {
}
