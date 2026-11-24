package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.port.GoogleOAuthVerifierPort;
import com.example.demo.domain.user.application.port.KakaoOAuthVerifierPort;
import com.example.demo.domain.user.application.result.LoginResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.entity.RefreshToken;
import com.example.demo.domain.user.domain.error.AuthErrorCode;
import com.example.demo.domain.user.domain.repository.RefreshTokenRepository;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.type.AccountStatus;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.security.TokenProvider;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class AuthServiceTest {

  private static final String SOCIAL_TOKEN = "social-token";

  private final KakaoOAuthVerifierPort kakaoOAuthVerifier = mock(KakaoOAuthVerifierPort.class);
  private final GoogleOAuthVerifierPort googleOAuthVerifier = mock(GoogleOAuthVerifierPort.class);
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
    RefreshToken previousToken =
        RefreshToken.builder().user(user).refreshToken("previous-refresh-token").build();
    when(refreshTokenRepository.findByUserId(1L)).thenReturn(Optional.of(previousToken));

    LoginResult result = authService.login(Provider.Kakao, SOCIAL_TOKEN);

    assertThat(result.isNewUser()).isFalse();
    assertThat(result.accessToken()).isEqualTo("access-token");
    assertThat(result.refreshToken()).isEqualTo("refresh-token");
    verify(googleOAuthVerifier, never()).verify(SOCIAL_TOKEN);
    verify(refreshTokenRepository).delete(previousToken);
    ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
    verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
    assertThat(refreshTokenCaptor.getValue().getRefreshToken()).isEqualTo("refresh-token");
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
    when(refreshTokenRepository.findByUserId(2L)).thenReturn(Optional.empty());

    LoginResult result = authService.login(Provider.Google, SOCIAL_TOKEN);

    assertThat(result.isNewUser()).isFalse();
    assertThat(result.accessToken()).isEqualTo("access-token");
    assertThat(result.refreshToken()).isEqualTo("refresh-token");
    verify(kakaoOAuthVerifier, never()).verify(SOCIAL_TOKEN);
    ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
    verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
    assertThat(refreshTokenCaptor.getValue().getRefreshToken()).isEqualTo("refresh-token");
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

  @Test
  void deletesSavedRefreshTokenOnLogout() {
    User user = User.builder().id(1L).provider(Provider.Google).providerId("google-user").build();
    RefreshToken savedToken =
        RefreshToken.builder().user(user).refreshToken("refresh-token").build();
    when(tokenProvider.getUserId("refresh-token")).thenReturn(1L);
    when(refreshTokenRepository.findByUserId(1L)).thenReturn(Optional.of(savedToken));

    authService.logout(1L, "refresh-token");

    verify(tokenProvider).validateRefreshTokenOrThrow("refresh-token");
    verify(refreshTokenRepository).delete(savedToken);
  }

  @Test
  void blocksLoginForSuspendedUser() {
    SocialUserInfo socialUserInfo =
        new SocialUserInfo(Provider.Kakao, "kakao-user", "카카오 사용자", "kakao@example.com");
    User user =
        User.builder()
            .id(1L)
            .provider(Provider.Kakao)
            .providerId("kakao-user")
            .accountStatus(AccountStatus.SUSPENDED)
            .build();
    when(kakaoOAuthVerifier.verify(SOCIAL_TOKEN)).thenReturn(socialUserInfo);
    when(userRepository.findByProviderAndProviderId(Provider.Kakao, "kakao-user"))
        .thenReturn(Optional.of(user));

    assertThatThrownBy(() -> authService.login(Provider.Kakao, SOCIAL_TOKEN))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode()).isEqualTo(AuthErrorCode.ACCOUNT_SUSPENDED));
    verify(tokenProvider, never()).createAccessToken(user);
    verify(tokenProvider, never()).createRefreshToken(user);
  }

  @Test
  void blocksRefreshForSuspendedUser() {
    User user =
        User.builder()
            .id(1L)
            .provider(Provider.Kakao)
            .providerId("kakao-user")
            .accountStatus(AccountStatus.SUSPENDED)
            .build();
    when(tokenProvider.getUserId("refresh-token")).thenReturn(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    assertThatThrownBy(() -> authService.refresh("refresh-token"))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode()).isEqualTo(AuthErrorCode.ACCOUNT_SUSPENDED));
    verify(tokenProvider).validateRefreshTokenOrThrow("refresh-token");
    verify(refreshTokenRepository, never()).findByUserId(1L);
  }

  @Test
  void allowsLoginAfterSuspensionIsReleased() {
    SocialUserInfo socialUserInfo =
        new SocialUserInfo(Provider.Kakao, "kakao-user", "user", "kakao@example.com");
    User user =
        User.builder()
            .id(1L)
            .provider(Provider.Kakao)
            .providerId("kakao-user")
            .accountStatus(AccountStatus.SUSPENDED)
            .build();
    user.releaseSuspension();
    when(kakaoOAuthVerifier.verify(SOCIAL_TOKEN)).thenReturn(socialUserInfo);
    when(userRepository.findByProviderAndProviderId(Provider.Kakao, "kakao-user"))
        .thenReturn(Optional.of(user));
    when(tokenProvider.createAccessToken(user)).thenReturn("access-token");
    when(tokenProvider.createRefreshToken(user)).thenReturn("refresh-token");
    when(refreshTokenRepository.findByUserId(1L)).thenReturn(Optional.empty());

    LoginResult result = authService.login(Provider.Kakao, SOCIAL_TOKEN);

    assertThat(result.accessToken()).isEqualTo("access-token");
    assertThat(result.refreshToken()).isEqualTo("refresh-token");
  }

  @Test
  void allowsExistingValidRefreshTokenAfterSuspensionIsReleased() {
    User user =
        User.builder()
            .id(1L)
            .provider(Provider.Kakao)
            .providerId("kakao-user")
            .accountStatus(AccountStatus.ACTIVE)
            .build();
    user.suspend(LocalDateTime.parse("2026-11-18T01:02:03"), 9L, "policy violation");
    user.releaseSuspension();
    RefreshToken savedToken =
        RefreshToken.builder().user(user).refreshToken("refresh-token").build();
    when(tokenProvider.getUserId("refresh-token")).thenReturn(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(refreshTokenRepository.findByUserId(1L)).thenReturn(Optional.of(savedToken));
    when(tokenProvider.createAccessToken(user)).thenReturn("new-access-token");

    String result = authService.refresh("refresh-token");

    assertThat(result).isEqualTo("new-access-token");
    verify(tokenProvider).validateRefreshTokenOrThrow("refresh-token");
  }
}
