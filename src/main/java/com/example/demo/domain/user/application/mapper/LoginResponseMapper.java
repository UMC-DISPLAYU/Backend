package com.example.demo.domain.user.application.mapper;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.result.LoginResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.presentation.response.LoginResponse;
import com.example.demo.domain.user.presentation.response.OAuthCallbackResponse;
import org.springframework.stereotype.Component;

@Component
public class LoginResponseMapper {

  public OAuthCallbackResponse toResponse(LoginResult result) {
    return result.isNewUser() ? toSignupResponse(result) : toLoginResponse(result);
  }

  public LoginResponse.Login toLoginResponse(LoginResult result) {

    User user = result.user();

    return new LoginResponse.Login(
        false,
        result.accessToken(),
        new LoginResponse.UserInfo(
            user.getId(),
            user.getProvider(),
            user.getProviderId(),
            user.getName(),
            user.getNickname(),
            user.getSocialEmail(),
            user.getSchoolEmail(),
            user.isVerified()));
  }

  public LoginResponse.Signup toSignupResponse(LoginResult result) {

    SocialUserInfo socialUserInfo = result.socialUserInfo();

    return new LoginResponse.Signup(
        true,
        result.signupToken(),
        socialUserInfo.provider(),
        socialUserInfo.name(),
        socialUserInfo.socialEmail());
  }
}
