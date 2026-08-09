package com.example.demo.domain.user.application.port;

import com.example.demo.domain.user.application.auth.SocialUserInfo;

public interface GoogleOAuthVerifierPort {

  SocialUserInfo verify(String idToken);
}
