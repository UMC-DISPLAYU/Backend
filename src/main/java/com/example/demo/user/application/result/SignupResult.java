package com.example.demo.user.application.result;

import com.example.demo.user.domain.entity.User;

public record SignupResult(User user, String accessToken, String refreshToken) {}
