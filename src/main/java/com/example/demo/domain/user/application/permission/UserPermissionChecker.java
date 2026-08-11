package com.example.demo.domain.user.application.permission;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.AuthErrorCode;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class UserPermissionChecker {

  public void requireActive(User user) {
    if (user.getDeletedAt() != null) {
      throw new BusinessException(AuthErrorCode.WITHDRAWAL_USER);
    }
  }
}
