package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.result.LoginResult;
import com.example.demo.domain.user.domain.entity.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.infrastructure.oauth.KakaoOAuthVerifier;
import com.example.demo.domain.user.presentation.request.SocialLoginRequest;
import com.example.demo.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final KakaoOAuthVerifier kakaoOAuthVerifier;
  private final UserRepository userRepository;
  private final TokenProvider tokenProvider;

  public LoginResult login(SocialLoginRequest request) {

    SocialUserInfo socialUserInfo = kakaoOAuthVerifier.verify(request.idToken());

    User user =
        userRepository
            .findByProviderAndProviderId(socialUserInfo.provider(), socialUserInfo.providerId())
            .orElse(null);

    // 기존 회원
    if (user != null) {

      String accessToken = tokenProvider.createAccessToken(user);

      String refreshToken = tokenProvider.createRefreshToken(user);

      return LoginResult.login(user, accessToken, refreshToken);
    }

    // 신규 회원
    String signupToken = tokenProvider.createSignupToken(socialUserInfo);

    return LoginResult.signup(signupToken, socialUserInfo);
  }
}
