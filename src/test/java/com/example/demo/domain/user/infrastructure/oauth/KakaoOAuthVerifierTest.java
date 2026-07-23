package com.example.demo.domain.user.infrastructure.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.exception.AuthErrorCode;
import com.example.demo.domain.user.infrastructure.oauth.dto.KakaoUserInfoResponse;
import com.example.demo.global.error.BusinessException;
import org.junit.jupiter.api.Test;

class KakaoOAuthVerifierTest {

  private final KakaoOAuthClient kakaoOAuthClient = mock(KakaoOAuthClient.class);
  private final KakaoOAuthVerifier verifier = new KakaoOAuthVerifier(kakaoOAuthClient);

  @Test
  void retrievesKakaoUserWithAccessToken() {
    KakaoUserInfoResponse response =
        new KakaoUserInfoResponse(
            1234L,
            null,
            new KakaoUserInfoResponse.KakaoAccount(
                "kakao@example.com", new KakaoUserInfoResponse.Profile("카카오 사용자"), false, false));
    when(kakaoOAuthClient.getUserInfo("access-token")).thenReturn(response);

    SocialUserInfo result = verifier.verify("access-token");

    assertThat(result.providerId()).isEqualTo("1234");
    assertThat(result.name()).isEqualTo("카카오 사용자");
    assertThat(result.socialEmail()).isEqualTo("kakao@example.com");
  }

  @Test
  void rejectsInvalidKakaoAccessToken() {
    when(kakaoOAuthClient.getUserInfo("invalid-token"))
        .thenThrow(new IllegalArgumentException("Unauthorized"));

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> verifier.verify("invalid-token"))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(AuthErrorCode.INVALID_SOCIAL_TOKEN));
  }

  @Test
  void rejectsKakaoUserInfoWithoutUserId() {
    when(kakaoOAuthClient.getUserInfo("access-token"))
        .thenReturn(new KakaoUserInfoResponse(null, null, null));

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> verifier.verify("access-token"))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(AuthErrorCode.INVALID_SOCIAL_TOKEN));
  }
}
