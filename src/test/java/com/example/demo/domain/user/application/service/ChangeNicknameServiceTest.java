package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.application.command.ChangeNicknameCommand;
import com.example.demo.domain.user.application.result.ChangeNicknameResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.vo.Nickname;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ChangeNicknameServiceTest {

  private static final Long USER_ID = 1L;
  private static final String NEW_NICKNAME = "User2";
  private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
  private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-07-19T11:00:00Z"), ZONE_ID);

  private final UserRepository userRepository = mock(UserRepository.class);
  private final ChangeNicknameService service = new ChangeNicknameService(userRepository, CLOCK);

  @Test
  void changesNicknameAndReturnsNextAvailableTime() {
    User user = User.builder().id(USER_ID).nickname("User1").build();
    prepareUser(user);

    ChangeNicknameResult result = service.execute(command());

    assertThat(user.getNickname()).isEqualTo(NEW_NICKNAME);
    assertThat(result.nickname()).isEqualTo(NEW_NICKNAME);
    assertThat(result.nextNicknameChangeAvailableAt())
        .isEqualTo(LocalDateTime.now(CLOCK).plusDays(30));
  }

  @Test
  void allowsChangeExactlyThirtyDaysAfterPreviousChange() {
    User user =
        User.builder()
            .id(USER_ID)
            .nickname("User1")
            .nicknameChangeAt(LocalDateTime.now(CLOCK).minusDays(30))
            .build();
    prepareUser(user);

    service.execute(command());

    assertThat(user.getNickname()).isEqualTo(NEW_NICKNAME);
  }

  @Test
  void rejectsChangeBeforeThirtyDays() {
    User user =
        User.builder()
            .id(USER_ID)
            .nickname("User1")
            .nicknameChangeAt(LocalDateTime.now(CLOCK).minusDays(29))
            .build();
    prepareUser(user);

    assertThatExceptionOfType(UserException.class)
        .isThrownBy(() -> service.execute(command()))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(UserErrorCode.NICKNAME_CHANGE_NOT_ALLOWED));
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
