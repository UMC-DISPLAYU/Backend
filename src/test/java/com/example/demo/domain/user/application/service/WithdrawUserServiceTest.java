package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.application.service.ExitDisplayService;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.repository.TeamMemberRepository;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.user.application.command.WithdrawUserCommand;
import com.example.demo.domain.user.application.port.UserWithdrawalCleanupPort;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.RefreshTokenRepository;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.global.error.BusinessException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class WithdrawUserServiceTest {

  private static final Long USER_ID = 1L;
  private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
  private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-07-19T11:00:00Z"), ZONE_ID);

  private final UserRepository userRepository = mock(UserRepository.class);
  private final RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
  private final TeamMemberRepository teamMemberRepository = mock(TeamMemberRepository.class);
  private final DisplayRepository displayRepository = mock(DisplayRepository.class);
  private final ExitDisplayService exitDisplayService = mock(ExitDisplayService.class);
  private final UserWithdrawalCleanupPort withdrawalCleanupPort =
      mock(UserWithdrawalCleanupPort.class);
  private final WithdrawUserService service =
      new WithdrawUserService(
          userRepository,
          refreshTokenRepository,
          teamMemberRepository,
          displayRepository,
          exitDisplayService,
          withdrawalCleanupPort,
          CLOCK);

  @Test
  void withdrawsUserImmediately() {
    User user = User.builder().id(USER_ID).build();
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(displayRepository.findParticipatedDisplaysByUserId(USER_ID)).thenReturn(List.of());
    when(refreshTokenRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

    service.execute(command());

    assertThat(user.getDeletedAt())
        .isEqualTo(LocalDateTime.ofInstant(CLOCK.instant(), ZoneOffset.UTC));
    verify(withdrawalCleanupPort).cleanup(USER_ID);
    verify(userRepository).flush();
  }

  @Test
  void exitsEveryParticipatingDisplayBeforeWithdrawal() {
    User user = User.builder().id(USER_ID).build();
    Display first = mock(Display.class);
    Display second = mock(Display.class);
    when(first.getId()).thenReturn(10L);
    when(second.getId()).thenReturn(20L);
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(displayRepository.findParticipatedDisplaysByUserId(USER_ID))
        .thenReturn(List.of(first, second));
    when(refreshTokenRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

    service.execute(command());

    verify(exitDisplayService).exit(10L, USER_ID);
    verify(exitDisplayService).exit(20L, USER_ID);
  }

  @Test
  void rejectsUserWhoLeadsAnActiveDisplayWithoutCleaningData() {
    User user = User.builder().id(USER_ID).build();
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(teamMemberRepository.existsActiveAcceptedByUserIdAndRole(
            USER_ID, TeamMemberRole.TEAM_LEADER))
        .thenReturn(true);

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.execute(command()))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayErrorCode.DISPLAY_LEADER_REMOVAL_NOT_ALLOWED));

    verify(withdrawalCleanupPort, never()).cleanup(USER_ID);
    verify(exitDisplayService, never())
        .exit(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyLong());
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
