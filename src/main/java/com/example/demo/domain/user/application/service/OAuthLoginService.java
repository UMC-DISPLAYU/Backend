package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.result.LoginResult;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.exception.AuthErrorCode;
import com.example.demo.domain.user.infrastructure.oauth.GoogleAuthorizationCodeClient;
import com.example.demo.domain.user.infrastructure.oauth.KakaoOAuthClient;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthLoginService {

  private final KakaoOAuthClient kakaoOAuthClient;
  private final GoogleAuthorizationCodeClient googleAuthorizationCodeClient;
  private final AuthService authService;

  public String authorizationUrl(Provider provider, String state) {
    return switch (provider) {
      case Kakao -> kakaoOAuthClient.authorizationUrl(state);
      case Google -> googleAuthorizationCodeClient.authorizationUrl(state);
    };
  }

  public LoginResult loginWithAuthorizationCode(Provider provider, String code) {
    try {
      String socialToken =
          switch (provider) {
            case Kakao -> kakaoOAuthClient.exchangeCode(code);
            case Google -> googleAuthorizationCodeClient.exchangeCode(code);
          };
      return authService.login(provider, socialToken);
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      log.warn(
          "OAuth authorization code login failed. provider={}, exception={}",
          provider,
          e.getClass().getSimpleName());
      throw new BusinessException(AuthErrorCode.INVALID_SOCIAL_TOKEN);
    }
  }

  public void validateState(String expectedState, String actualState) {
    if (!StringUtils.hasText(expectedState) || !expectedState.equals(actualState)) {
      throw new BusinessException(AuthErrorCode.INVALID_SOCIAL_TOKEN);
    }
  }
}
