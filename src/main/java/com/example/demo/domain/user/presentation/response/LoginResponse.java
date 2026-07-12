package com.example.demo.domain.user.presentation.response;

import com.example.demo.domain.user.domain.enums.Provider;

public class LoginResponse {

  public record Login(boolean isNewUser, String accessToken, String refreshToken, UserInfo user) {}

  public record Signup(
      boolean isNewUser, String signupToken, Provider provider, String name, String socialEmail) {}

  public record UserInfo(
      Long id,
      Provider provider,
      String providerId,
      String name,
      String nickname,
      String socialEmail,
      String schoolEmail,
      boolean isVerified) {}
}
