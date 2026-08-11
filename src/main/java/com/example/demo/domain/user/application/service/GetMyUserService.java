package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.result.MyUserResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.AuthErrorCode;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMyUserService {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public MyUserResult execute(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    if (user.getDeletedAt() != null) {
      throw new BusinessException(AuthErrorCode.WITHDRAWAL_USER);
    }

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
