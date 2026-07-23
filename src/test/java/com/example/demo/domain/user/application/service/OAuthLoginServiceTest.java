package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.exception.AuthErrorCode;
import com.example.demo.domain.user.infrastructure.oauth.GoogleAuthorizationCodeClient;
import com.example.demo.domain.user.infrastructure.oauth.KakaoOAuthClient;
import com.example.demo.global.error.BusinessException;
import org.junit.jupiter.api.Test;

class OAuthLoginServiceTest {

  private final KakaoOAuthClient kakaoOAuthClient = mock(KakaoOAuthClient.class);
  private final GoogleAuthorizationCodeClient googleAuthorizationCodeClient =
      mock(GoogleAuthorizationCodeClient.class);
  private final AuthService authService = mock(AuthService.class);
  private final OAuthLoginService service =
      new OAuthLoginService(kakaoOAuthClient, googleAuthorizationCodeClient, authService);

  @Test
  void exchangesKakaoCodeForAccessToken() {
    when(kakaoOAuthClient.exchangeCode("code")).thenReturn("kakao-access-token");

    service.loginWithAuthorizationCode(Provider.Kakao, "code");

    verify(authService).login(Provider.Kakao, "kakao-access-token");
  }

  @Test
  void exchangesGoogleCodeForIdToken() {
    when(googleAuthorizationCodeClient.exchangeCode("code")).thenReturn("google-id-token");

    service.loginWithAuthorizationCode(Provider.Google, "code");

    verify(authService).login(Provider.Google, "google-id-token");
  }

  @Test
  void rejectsMismatchedState() {
    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.validateState("expected", "actual"))
        .satisfies(
            exception ->
                org.assertj.core.api.Assertions.assertThat(exception.errorCode())
                    .isEqualTo(AuthErrorCode.INVALID_SOCIAL_TOKEN));
  }
}
