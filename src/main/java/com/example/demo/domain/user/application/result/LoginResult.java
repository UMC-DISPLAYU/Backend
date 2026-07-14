package com.example.demo.domain.user.application.result;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.domain.entity.User;

public record LoginResult(
    User user,
    String accessToken,
    String refreshToken,
    String signupToken,
    SocialUserInfo socialUserInfo) {

  public static LoginResult login(User user, String accessToken, String refreshToken) {
    return new LoginResult(user, accessToken, refreshToken, null, null);
  }

  public static LoginResult signup(String signupToken, SocialUserInfo socialUserInfo) {
    return new LoginResult(null, null, null, signupToken, socialUserInfo);
  }
}
