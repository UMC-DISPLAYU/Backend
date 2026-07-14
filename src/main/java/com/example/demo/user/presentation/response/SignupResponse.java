package com.example.demo.user.presentation.response;

import lombok.Builder;

public class SignupResponse {

  @Builder
  public record Signup(UserInfo user, String accessToken, String refreshToken) {}

  @Builder
  public record UserInfo(
      Long id,
      String provider,
      String name,
      String nickname,
      String socialEmail,
      String schoolEmail,
      boolean isVerified) {}
}
