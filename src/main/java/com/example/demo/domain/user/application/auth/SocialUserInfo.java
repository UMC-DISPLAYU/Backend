package com.example.demo.domain.user.application.auth;

import com.example.demo.domain.user.domain.type.Provider;

public record SocialUserInfo(
    Provider provider, String providerId, String name, String socialEmail) {}
