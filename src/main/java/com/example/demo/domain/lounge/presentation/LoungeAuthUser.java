package com.example.demo.domain.lounge.presentation;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.security.AuthUser;

final class LoungeAuthUser {

  private LoungeAuthUser() {}

  static Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
