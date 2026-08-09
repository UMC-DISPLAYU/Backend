package com.example.demo.global.security;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.domain.error.AuthErrorCode;
import com.example.demo.global.error.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenProvider {

  private final JwtFactory jwtFactory;
  private final JwtProperties jwtProperties;

  public String createAccessToken(User user) {

    return jwtFactory.create(
        user.getId().toString(), jwtProperties.getAccessExpiration(), "ACCESS");
  }

  public String createRefreshToken(User user) {

    return jwtFactory.create(
        user.getId().toString(), jwtProperties.getRefreshExpiration(), "REFRESH");
  }

  public String createSignupToken(SocialUserInfo socialUserInfo) {

    return jwtFactory.createSignupToken(socialUserInfo);
  }

  public SocialUserInfo parseSignupToken(String signupToken) {

    try {

      Claims claims = jwtFactory.parse(signupToken);

      validateTokenType(claims, "SIGNUP");

      return new SocialUserInfo(
          Provider.valueOf(claims.get("provider", String.class)),
          claims.get("providerId", String.class),
          claims.get("name", String.class),
          claims.get("socialEmail", String.class));

    } catch (Exception e) {

      throw new BusinessException(AuthErrorCode.INVALID_SIGNUP_TOKEN);
    }
  }

  public void validateAccessTokenOrThrow(String token) {

    validateToken(
        token, "ACCESS", AuthErrorCode.INVALID_ACCESS_TOKEN, AuthErrorCode.EXPIRED_ACCESS_TOKEN);
  }

  public void validateRefreshTokenOrThrow(String token) {

    validateToken(
        token, "REFRESH", AuthErrorCode.INVALID_REFRESH_TOKEN, AuthErrorCode.EXPIRED_REFRESH_TOKEN);
  }

  private void validateToken(
      String token, String type, AuthErrorCode invalidError, AuthErrorCode expiredError) {

    try {

      Claims claims = jwtFactory.parse(token);

      validateTokenType(claims, type);

    } catch (ExpiredJwtException e) {

      throw new BusinessException(expiredError);

    } catch (BusinessException e) {

      throw e;

    } catch (Exception e) {

      throw new BusinessException(invalidError);
    }
  }

  private void validateTokenType(Claims claims, String expectedType) {

    if (!expectedType.equals(claims.get("type", String.class))) {

      throw new BusinessException(AuthErrorCode.INVALID_ACCESS_TOKEN);
    }
  }

  public Long getUserId(String token) {

    try {

      Claims claims = jwtFactory.parse(token);

      return Long.parseLong(claims.getSubject());

    } catch (Exception e) {

      throw new BusinessException(AuthErrorCode.INVALID_ACCESS_TOKEN);
    }
  }
}
