package com.example.demo.global.security;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.domain.entity.User;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.exception.AuthErrorCode;
import com.example.demo.global.error.BusinessException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenProvider {

  private final JwtFactory jwtFactory;
  private final JwtProperties jwtProperties;

  public String createAccessToken(User user) {

    return jwtFactory.create(user.getId().toString(), jwtProperties.getAccessExpiration());
  }

  public String createRefreshToken(User user) {

    return jwtFactory.create(user.getId().toString(), jwtProperties.getRefreshExpiration());
  }

  public String createSignupToken(SocialUserInfo socialUserInfo) {

    return jwtFactory.createSignupToken(socialUserInfo);
  }

  public SocialUserInfo parseSignupToken(String signupToken) {

    try {

      Claims claims = jwtFactory.parse(signupToken);

      return new SocialUserInfo(
          Provider.valueOf(claims.get("provider", String.class)),
          claims.get("providerId", String.class),
          claims.get("name", String.class),
          claims.get("socialEmail", String.class));

    } catch (Exception e) {

      throw new BusinessException(AuthErrorCode.INVALID_SIGNUP_TOKEN);
    }
  }
}
