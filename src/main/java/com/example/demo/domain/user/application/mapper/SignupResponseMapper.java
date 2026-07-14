package com.example.demo.domain.user.application.mapper;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.presentation.response.SignupResponse;
import org.springframework.stereotype.Component;

@Component
public class SignupResponseMapper {

  public SignupResponse.Signup toResponse(User user, String accessToken, String refreshToken) {

    return SignupResponse.Signup.builder()
        .user(toUserInfo(user))
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  private SignupResponse.UserInfo toUserInfo(User user) {

    return SignupResponse.UserInfo.builder()
        .id(user.getId())
        .provider(user.getProvider().name())
        .name(user.getName())
        .nickname(user.getNickname())
        .socialEmail(user.getSocialEmail())
        .schoolEmail(user.getSchoolEmail())
        .isVerified(user.isVerified())
        .build();
  }
}
