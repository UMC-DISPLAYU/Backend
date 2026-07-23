package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.result.LoginResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.domain.repository.RefreshTokenRepository;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.infrastructure.oauth.GoogleOAuthVerifier;
import com.example.demo.domain.user.infrastructure.oauth.KakaoOAuthVerifier;
import com.example.demo.global.security.TokenProvider;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuthServiceTest {

  private static final String SOCIAL_TOKEN = "social-token";

  private final KakaoOAuthVerifier kakaoOAuthVerifier = mock(KakaoOAuthVerifier.class);
  private final GoogleOAuthVerifier googleOAuthVerifier = mock(GoogleOAuthVerifier.class);
  private final UserRepository userRepository = mock(UserRepository.class);
  private final RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
  private final TokenProvider tokenProvider = mock(TokenProvider.class);
  private final AuthService authService =
      new AuthService(
          kakaoOAuthVerifier,
          googleOAuthVerifier,
          userRepository,
          refreshTokenRepository,
          tokenProvider);

  @Test
  void logsInExistingKakaoUserWithKakaoVerifier() {
    SocialUserInfo socialUserInfo =
        new SocialUserInfo(Provider.Kakao, "kakao-user", "카카오 사용자", "kakao@example.com");
    User user = User.builder().id(1L).provider(Provider.Kakao).providerId("kakao-user").build();
    when(kakaoOAuthVerifier.verify(SOCIAL_TOKEN)).thenReturn(socialUserInfo);
    when(userRepository.findByProviderAndProviderId(Provider.Kakao, "kakao-user"))
        .thenReturn(Optional.of(user));
    when(tokenProvider.createAccessToken(user)).thenReturn("access-token");
    when(tokenProvider.createRefreshToken(user)).thenReturn("refresh-token");

    LoginResult result = authService.login(Provider.Kakao, SOCIAL_TOKEN);

    assertThat(result.isNewUser()).isFalse();
    assertThat(result.accessToken()).isEqualTo("access-token");
    assertThat(result.refreshToken()).isEqualTo("refresh-token");
    verify(googleOAuthVerifier, never()).verify(SOCIAL_TOKEN);
  }

  @Test
  void preparesSignupForNewKakaoUser() {
    SocialUserInfo socialUserInfo =
        new SocialUserInfo(Provider.Kakao, "kakao-user", "카카오 사용자", "kakao@example.com");
    when(kakaoOAuthVerifier.verify(SOCIAL_TOKEN)).thenReturn(socialUserInfo);
    when(userRepository.findByProviderAndProviderId(Provider.Kakao, "kakao-user"))
        .thenReturn(Optional.empty());
    when(tokenProvider.createSignupToken(socialUserInfo)).thenReturn("signup-token");

    LoginResult result = authService.login(Provider.Kakao, SOCIAL_TOKEN);

    assertThat(result.isNewUser()).isTrue();
    assertThat(result.signupToken()).isEqualTo("signup-token");
  }

  @Test
  void logsInExistingGoogleUserWithGoogleVerifier() {
    SocialUserInfo socialUserInfo =
        new SocialUserInfo(Provider.Google, "google-user", "구글 사용자", "google@example.com");
    User user = User.builder().id(2L).provider(Provider.Google).providerId("google-user").build();
    when(googleOAuthVerifier.verify(SOCIAL_TOKEN)).thenReturn(socialUserInfo);
    when(userRepository.findByProviderAndProviderId(Provider.Google, "google-user"))
        .thenReturn(Optional.of(user));
    when(tokenProvider.createAccessToken(user)).thenReturn("access-token");
    when(tokenProvider.createRefreshToken(user)).thenReturn("refresh-token");

    LoginResult result = authService.login(Provider.Google, SOCIAL_TOKEN);

    assertThat(result.isNewUser()).isFalse();
    assertThat(result.accessToken()).isEqualTo("access-token");
    assertThat(result.refreshToken()).isEqualTo("refresh-token");
    verify(kakaoOAuthVerifier, never()).verify(SOCIAL_TOKEN);
  }

  @Test
  void preparesSignupForNewGoogleUser() {
    SocialUserInfo socialUserInfo =
        new SocialUserInfo(Provider.Google, "google-user", "구글 사용자", "google@example.com");
    when(googleOAuthVerifier.verify(SOCIAL_TOKEN)).thenReturn(socialUserInfo);
    when(userRepository.findByProviderAndProviderId(Provider.Google, "google-user"))
        .thenReturn(Optional.empty());
    when(tokenProvider.createSignupToken(socialUserInfo)).thenReturn("signup-token");

    LoginResult result = authService.login(Provider.Google, SOCIAL_TOKEN);

    assertThat(result.isNewUser()).isTrue();
    assertThat(result.signupToken()).isEqualTo("signup-token");
  }
}
