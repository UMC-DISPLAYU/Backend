package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.port.GoogleAuthorizationCodeClientPort;
import com.example.demo.domain.user.application.port.KakaoOAuthClientPort;
import com.example.demo.domain.user.application.result.LoginResult;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.domain.error.AuthErrorCode;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthLoginService {

  private final KakaoOAuthClientPort kakaoOAuthClient;
  private final GoogleAuthorizationCodeClientPort googleAuthorizationCodeClient;
  private final AuthService authService;

  public String authorizationUrl(Provider provider, String state) {
    return switch (provider) {
      case Kakao -> kakaoOAuthClient.authorizationUrl(state);
      case Google -> googleAuthorizationCodeClient.authorizationUrl(state);
    };
  }

  public LoginResult loginWithAuthorizationCode(Provider provider, String code) {
    if (!StringUtils.hasText(code)) {
      log.warn("OAuth authorization code is missing. provider={}", provider);
      throw new BusinessException(AuthErrorCode.INVALID_SOCIAL_TOKEN);
    }
    try {
      String socialToken =
          switch (provider) {
            case Kakao -> kakaoOAuthClient.exchangeCode(code);
            case Google -> googleAuthorizationCodeClient.exchangeCode(code);
          };
      log.info(
          "OAuth provider token exchange completed. provider={}, socialTokenPresent={}",
          provider,
          StringUtils.hasText(socialToken));
      return authService.login(provider, socialToken);
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      log.warn(
          "OAuth authorization code login failed. provider={}, authorizationCodePresent={}, "
              + "exception={}, message={}",
          provider,
          true,
          e.getClass().getSimpleName(),
          e.getMessage());
      throw new BusinessException(AuthErrorCode.INVALID_SOCIAL_TOKEN);
    }
  }

  public void validateState(String expectedState, String actualState) {
    if (!StringUtils.hasText(expectedState) || !expectedState.equals(actualState)) {
      log.warn(
          "OAuth state validation failed. expectedStatePresent={}, actualStatePresent={}, matched={}",
          StringUtils.hasText(expectedState),
          StringUtils.hasText(actualState),
          false);
      throw new BusinessException(AuthErrorCode.INVALID_SOCIAL_TOKEN);
    }
    log.debug("OAuth state validation completed.");
  }
}
