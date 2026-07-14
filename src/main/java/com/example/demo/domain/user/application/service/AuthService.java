package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.result.LoginResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.entity.RefreshToken;
import com.example.demo.domain.user.domain.repository.RefreshTokenRepository;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.exception.AuthErrorCode;
import com.example.demo.domain.user.infrastructure.oauth.GoogleOAuthVerifier;
import com.example.demo.domain.user.infrastructure.oauth.KakaoOAuthVerifier;
import com.example.demo.domain.user.presentation.request.SocialLoginRequest;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final KakaoOAuthVerifier kakaoOAuthVerifier;
  private final GoogleOAuthVerifier googleOAuthVerifier;

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  private final TokenProvider tokenProvider;

  public LoginResult login(SocialLoginRequest request) {

    SocialUserInfo socialUserInfo;

    switch (request.provider()) {
      case Kakao -> socialUserInfo = kakaoOAuthVerifier.verify(request.idToken());

      case Google -> socialUserInfo = googleOAuthVerifier.verify(request.idToken());

      default -> throw new BusinessException(AuthErrorCode.INVALID_SOCIAL_TOKEN);
    }

    User user =
        userRepository
            .findByProviderAndProviderId(socialUserInfo.provider(), socialUserInfo.providerId())
            .orElse(null);

    // 기존 회원
    if (user != null) {

      String accessToken = tokenProvider.createAccessToken(user);

      String refreshToken = tokenProvider.createRefreshToken(user);

      saveRefreshToken(user, refreshToken);

      return LoginResult.login(user, accessToken, refreshToken);
    }

    // 신규 회원
    String signupToken = tokenProvider.createSignupToken(socialUserInfo);

    return LoginResult.signup(signupToken, socialUserInfo);
  }

  public String refresh(String refreshToken) {

    tokenProvider.validateRefreshTokenOrThrow(refreshToken);

    Long userId = tokenProvider.getUserId(refreshToken);

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND));

    if (user.getDeletedAt() != null) {

      throw new BusinessException(AuthErrorCode.WITHDRAWAL_USER);
    }

    RefreshToken savedToken =
        refreshTokenRepository
            .findByUserId(userId)
            .orElseThrow(() -> new BusinessException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND));

    if (!savedToken.getRefreshToken().equals(refreshToken)) {

      throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
    }

    return tokenProvider.createAccessToken(user);
  }

  /** AccessToken 인증은 Spring Security 담당 */
  public void logout(Long userId, String refreshToken) {

    tokenProvider.validateRefreshTokenOrThrow(refreshToken);

    Long refreshUserId = tokenProvider.getUserId(refreshToken);

    if (!userId.equals(refreshUserId)) {

      throw new BusinessException(AuthErrorCode.TOKEN_USER_MISMATCH);
    }

    RefreshToken savedToken =
        refreshTokenRepository
            .findByUserId(userId)
            .orElseThrow(() -> new BusinessException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND));

    if (!savedToken.getRefreshToken().equals(refreshToken)) {

      throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
    }

    refreshTokenRepository.delete(savedToken);
  }

  private void saveRefreshToken(User user, String refreshToken) {

    refreshTokenRepository.findByUserId(user.getId()).ifPresent(refreshTokenRepository::delete);

    refreshTokenRepository.save(
        RefreshToken.builder().user(user).refreshToken(refreshToken).build());
  }
}
