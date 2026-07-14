package com.example.demo.domain.user.application.result;

import com.example.demo.domain.user.domain.aggregate.User;

public record SignupResult(User user, String accessToken, String refreshToken) {}
