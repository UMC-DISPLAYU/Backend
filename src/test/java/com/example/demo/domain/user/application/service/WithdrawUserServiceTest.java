package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.application.command.WithdrawUserCommand;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.RefreshTokenRepository;
import com.example.demo.domain.user.domain.repository.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class WithdrawUserServiceTest {

  private static final Long USER_ID = 1L;
  private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
  private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-07-19T11:00:00Z"), ZONE_ID);

  private final UserRepository userRepository = mock(UserRepository.class);
  private final RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
  private final WithdrawUserService service =
      new WithdrawUserService(userRepository, refreshTokenRepository, CLOCK);

  @Test
  void withdrawsUserImmediately() {
    User user = User.builder().id(USER_ID).build();
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(refreshTokenRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

    service.execute(command());

    assertThat(user.getDeletedAt())
        .isEqualTo(LocalDateTime.ofInstant(CLOCK.instant(), ZoneOffset.UTC));
  }

  @Test
  void rejectsAlreadyWithdrawnUser() {
    User user =
        User.builder().id(USER_ID).deletedAt(LocalDateTime.parse("2026-07-18T20:00:00")).build();
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    assertThatExceptionOfType(UserException.class)
        .isThrownBy(() -> service.execute(command()))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(UserErrorCode.ALREADY_WITHDRAWN_USER));
  }

  @Test
  void rejectsMissingUser() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

    assertThatExceptionOfType(UserException.class)
        .isThrownBy(() -> service.execute(command()))
        .satisfies(
            exception -> assertThat(exception.errorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND));
  }

  private WithdrawUserCommand command() {
    return new WithdrawUserCommand(USER_ID);
  }
}
