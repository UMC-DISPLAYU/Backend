package com.example.demo.global.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.domain.enums.Provider;
import org.junit.jupiter.api.Test;

class TokenProviderTest {

  @Test
  void restoresAllSocialUserInfoFromSignupToken() {
    JwtProperties properties = new JwtProperties();
    properties.setSecret("test-secret-test-secret-test-secret-test-secret");
    properties.setSignupExpiration(600000);
    JwtFactory jwtFactory = new JwtFactory(properties);
    TokenProvider tokenProvider = new TokenProvider(jwtFactory, properties);
    SocialUserInfo original =
        new SocialUserInfo(Provider.Google, "google-provider-id", "구글 사용자", "google@example.com");

    String signupToken = tokenProvider.createSignupToken(original);
    SocialUserInfo restored = tokenProvider.parseSignupToken(signupToken);

    assertThat(restored).isEqualTo(original);
  }
}
