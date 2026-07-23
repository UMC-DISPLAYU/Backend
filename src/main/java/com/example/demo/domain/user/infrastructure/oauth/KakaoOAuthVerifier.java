package com.example.demo.domain.user.infrastructure.oauth;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.exception.AuthErrorCode;
import com.example.demo.domain.user.infrastructure.oauth.dto.KakaoUserInfoResponse;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class KakaoOAuthVerifier {

  private final KakaoOAuthClient kakaoOAuthClient;

  public SocialUserInfo verify(String accessToken) {

    try {
      KakaoUserInfoResponse userInfo = kakaoOAuthClient.getUserInfo(accessToken);

      if (userInfo == null || userInfo.id() == null) {
        throw new IllegalArgumentException("Kakao user info does not contain a user ID.");
      }
      if (!StringUtils.hasText(userInfo.nickname()) || !StringUtils.hasText(userInfo.email())) {
        throw new IllegalArgumentException("Kakao user info is missing required profile data.");
      }

      return new SocialUserInfo(
          Provider.Kakao, userInfo.id().toString(), userInfo.nickname(), userInfo.email());

    } catch (Exception e) {

      throw new BusinessException(AuthErrorCode.INVALID_SOCIAL_TOKEN);
    }
  }
}
