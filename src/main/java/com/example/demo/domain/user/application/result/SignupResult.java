package com.example.demo.domain.user.application.result;

import com.example.demo.domain.user.domain.entity.User;

public record SignupResult(User user, String accessToken, String refreshToken) {}
