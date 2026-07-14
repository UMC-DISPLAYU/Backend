package com.example.demo.domain.user.presentation.request;

import com.example.demo.domain.user.domain.enums.Provider;

public record SocialLoginRequest(Provider provider, String idToken) {}
