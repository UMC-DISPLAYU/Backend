package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.command.WithdrawUserCommand;
import com.example.demo.domain.user.contract.event.v1.UserWithdrawnEvent;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.RefreshTokenRepository;
import com.example.demo.domain.user.domain.repository.UserRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WithdrawUserService {

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final Clock clock;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void execute(WithdrawUserCommand command) {
    User user =
        userRepository
            .findById(command.userId())
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    user.withdraw(LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
    refreshTokenRepository.findByUserId(command.userId()).ifPresent(refreshTokenRepository::delete);
    eventPublisher.publishEvent(
        new UserWithdrawnEvent(
            UUID.randomUUID(), user.getId(), user.getQualificationRevision(), clock.instant()));
  }
}
