package com.example.demo.domain.user.application.service;

import com.example.demo.domain.display.application.service.ExitDisplayService;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WithdrawUserService {

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final TeamMemberRepository teamMemberRepository;
  private final DisplayRepository displayRepository;
  private final ExitDisplayService exitDisplayService;
  private final UserWithdrawalCleanupPort withdrawalCleanupPort;
  private final Clock clock;

  @Transactional
  public void execute(WithdrawUserCommand command) {
    User user =
        userRepository
            .findById(command.userId())
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    if (user.getDeletedAt() != null) {
      throw new UserException(UserErrorCode.ALREADY_WITHDRAWN_USER);
    }
    if (teamMemberRepository.existsActiveAcceptedByUserIdAndRole(
        command.userId(), TeamMemberRole.TEAM_LEADER)) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_LEADER_REMOVAL_NOT_ALLOWED);
    }

    displayRepository
        .findParticipatedDisplaysByUserId(command.userId())
        .forEach(display -> exitDisplayService.exit(display.getId(), command.userId()));
    withdrawalCleanupPort.cleanup(command.userId());
    user.withdraw(LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
    refreshTokenRepository.findByUserId(command.userId()).ifPresent(refreshTokenRepository::delete);
    userRepository.flush();
  }
}
