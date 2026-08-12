package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.application.command.ChangeNicknameCommand;
import com.example.demo.domain.user.application.result.ChangeNicknameResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.vo.Nickname;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ChangeNicknameServiceTest {

  private static final Long USER_ID = 1L;
  private static final String NEW_NICKNAME = "User2";
  private final UserRepository userRepository = mock(UserRepository.class);
  private final ChangeNicknameService service = new ChangeNicknameService(userRepository);

  @Test
  void changesNickname() {
    User user = User.builder().id(USER_ID).nickname("User1").build();
    prepareUser(user);

    ChangeNicknameResult result = service.execute(command());

    assertThat(user.getNickname()).isEqualTo(NEW_NICKNAME);
    assertThat(result.nickname()).isEqualTo(NEW_NICKNAME);
  }

  @Test
  void allowsChangeRegardlessOfPreviousChangeTime() {
    User user =
        User.builder().id(USER_ID).nickname("User1").nicknameChangeAt(LocalDateTime.now()).build();
    prepareUser(user);

    service.execute(command());

    assertThat(user.getNickname()).isEqualTo(NEW_NICKNAME);
  }

  @Test
  void rejectsDuplicateNickname() {
    when(userRepository.findById(USER_ID))
        .thenReturn(Optional.of(User.builder().id(USER_ID).build()));
    when(userRepository.existsByNickname(NEW_NICKNAME)).thenReturn(true);

    assertThatExceptionOfType(UserException.class)
        .isThrownBy(() -> service.execute(command()))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(UserErrorCode.DUPLICATE_NICKNAME));
  }

  @Test
  void skipsNicknamePolicyWhenNicknameIsUnchanged() {
    User user = User.builder().id(USER_ID).nickname(NEW_NICKNAME).build();
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    ChangeNicknameResult result = service.execute(command());

    assertThat(result.nickname()).isEqualTo(NEW_NICKNAME);
    verify(userRepository, never()).existsByNickname(NEW_NICKNAME);
    verify(userRepository, never()).flush();
  }

  @Test
  void rejectsMissingUser() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

    assertThatExceptionOfType(UserException.class)
        .isThrownBy(() -> service.execute(command()))
        .satisfies(
            exception -> assertThat(exception.errorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND));
  }

  @Test
  void rejectsInvalidNicknameFormat() {
    assertThatExceptionOfType(UserException.class)
        .isThrownBy(() -> new ChangeNicknameCommand(USER_ID, Nickname.of("invalid nickname")))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(UserErrorCode.INVALID_NICKNAME_FORMAT));
  }

  private void prepareUser(User user) {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(userRepository.existsByNickname(NEW_NICKNAME)).thenReturn(false);
  }

  private ChangeNicknameCommand command() {
    return new ChangeNicknameCommand(USER_ID, Nickname.of(NEW_NICKNAME));
  }
}
