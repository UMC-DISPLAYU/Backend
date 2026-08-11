package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.command.ChangeNicknameCommand;
import com.example.demo.domain.user.application.result.ChangeNicknameResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.vo.Nickname;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangeNicknameService {

  private final UserRepository userRepository;
  private final Clock clock;

  @Transactional
  public ChangeNicknameResult execute(ChangeNicknameCommand command) {
    User user =
        userRepository
            .findById(command.userId())
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    return changeNickname(user, command.nickname());
  }

  public ChangeNicknameResult changeNickname(User user, Nickname nickname) {
    if (Objects.equals(user.getNickname(), nickname.value())) {
      return new ChangeNicknameResult(user.getNickname(), user.getNextNicknameChangeAvailableAt());
    }

    if (userRepository.existsByNickname(nickname.value())) {
      throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
    }

    try {
      LocalDateTime nextChangeAvailableAt = user.changeNickname(nickname, LocalDateTime.now(clock));

      userRepository.flush();

      return new ChangeNicknameResult(nickname.value(), nextChangeAvailableAt);

    } catch (DataIntegrityViolationException e) {
      throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
    }
  }
}
