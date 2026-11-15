package com.example.demo.domain.admin.infrastructure.adapter;

import com.example.demo.domain.admin.application.port.UserStatusPort;
import com.example.demo.domain.admin.application.result.UserAccountStatusResult;
import com.example.demo.domain.admin.application.result.UserStatusResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.type.AccountStatus;
import com.example.demo.domain.user.domain.type.UserRole;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserStatusAdapter implements UserStatusPort {

  private final UserRepository userRepository;
  private final Clock clock;

  @Override
  @Transactional(readOnly = true)
  public UserAccountStatusResult getStatus(Long userId) {
    User user = findAvailableUser(userId);
    if (user.getAccountStatus() == AccountStatus.ACTIVE) {
      return new UserAccountStatusResult(
          user.getId(), AccountStatus.ACTIVE.name(), null, null, null);
    }
    return new UserAccountStatusResult(
        user.getId(),
        user.getAccountStatus().name(),
        toInstant(user.getSuspendedAt()),
        user.getSuspensionOperatorId(),
        user.getSuspensionReason());
  }

  @Override
  @Transactional
  public UserStatusResult changeStatus(
      Long userId, Long operatorId, String accountStatus, String reason) {
    User user =
        userRepository
            .findByIdForUpdate(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    requireNotWithdrawn(user);
    if (user.getRole() == UserRole.ADMIN) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }

    Instant processedAt = clock.instant();
    if (AccountStatus.SUSPENDED.name().equals(accountStatus)) {
      user.suspend(LocalDateTime.ofInstant(processedAt, ZoneOffset.UTC), operatorId, reason);
    } else {
      user.releaseSuspension();
    }
    userRepository.save(user);
    userRepository.flush();
    return new UserStatusResult(user.getId(), user.getAccountStatus().name(), processedAt);
  }

  private User findAvailableUser(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    requireNotWithdrawn(user);
    return user;
  }

  private void requireNotWithdrawn(User user) {
    if (user.getDeletedAt() != null) {
      throw new UserException(UserErrorCode.ALREADY_WITHDRAWN_USER);
    }
  }

  private Instant toInstant(LocalDateTime value) {
    return value == null ? null : value.toInstant(ZoneOffset.UTC);
  }
}
