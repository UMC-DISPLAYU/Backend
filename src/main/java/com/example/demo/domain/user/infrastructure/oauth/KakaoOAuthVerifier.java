package com.example.demo.domain.user.infrastructure.oauth;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.exception.AuthErrorCode;
import com.example.demo.domain.user.infrastructure.oauth.dto.KakaoUserInfoResponse;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthVerifier {

  private final KakaoOAuthClient kakaoOAuthClient;

  public SocialUserInfo verify(String accessToken) {

    try {
      KakaoUserInfoResponse userInfo = kakaoOAuthClient.getUserInfo(accessToken);

      if (userInfo == null || userInfo.id() == null) {
        log.warn("Kakao user info is missing the required user ID.");
        throw new IllegalArgumentException("Kakao user info does not contain a user ID.");
      }
      boolean hasNickname = StringUtils.hasText(userInfo.nickname());
      boolean hasEmail = StringUtils.hasText(userInfo.email());
      if (!hasNickname || !hasEmail) {
        log.warn(
            "Kakao user info is missing required profile data. hasNickname={}, hasEmail={}, "
                + "nicknameNeedsAgreement={}, emailNeedsAgreement={}",
            hasNickname,
            hasEmail,
            userInfo.kakaoAccount() == null
                ? null
                : userInfo.kakaoAccount().profileNicknameNeedsAgreement(),
            userInfo.kakaoAccount() == null ? null : userInfo.kakaoAccount().emailNeedsAgreement());
        throw new IllegalArgumentException("Kakao user info is missing required profile data.");
      }

      return new SocialUserInfo(
          Provider.Kakao, userInfo.id().toString(), userInfo.nickname(), userInfo.email());

    } catch (Exception e) {
      log.warn(
          "Kakao access token verification failed. exception={}, message={}",
          e.getClass().getSimpleName(),
          e.getMessage());
      throw new BusinessException(AuthErrorCode.INVALID_SOCIAL_TOKEN);
    }
  }
}
