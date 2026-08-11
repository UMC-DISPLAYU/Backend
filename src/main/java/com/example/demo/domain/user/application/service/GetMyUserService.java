package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.permission.UserPermissionChecker;
import com.example.demo.domain.user.application.result.MyUserResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMyUserService {

  private final UserRepository userRepository;
  private final UserPermissionChecker permissionChecker;

  @Transactional(readOnly = true)
  public MyUserResult execute(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    permissionChecker.requireActive(user);

    return new MyUserResult(
        user.getId(),
        user.getProvider(),
        user.getName(),
        user.getNickname(),
        user.getProfileImageUrl(),
        user.isVerified(),
        user.getSocialEmail(),
        user.getSchoolEmail());
  }
}
