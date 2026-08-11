package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.command.UpdateMyProfileCommand;
import com.example.demo.domain.user.application.result.UpdateMyProfileResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateMyProfileService {

  private final UserRepository userRepository;
  private final ChangeNicknameService changeNicknameService;

  @Transactional
  public UpdateMyProfileResult execute(UpdateMyProfileCommand command) {
    User user =
        userRepository
            .findById(command.userId())
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    changeNicknameService.changeNickname(user, command.nickname());
    user.changeProfileImage(command.profileImageUrl().value());

    return new UpdateMyProfileResult(user.getProfileImageUrl(), user.getNickname());
  }
}
