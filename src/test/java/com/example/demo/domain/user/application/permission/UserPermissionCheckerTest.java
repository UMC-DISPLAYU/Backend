package com.example.demo.domain.user.application.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.AuthErrorCode;
import com.example.demo.global.error.BusinessException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class UserPermissionCheckerTest {

  private final UserPermissionChecker checker = new UserPermissionChecker();

  @Test
  void requireActiveAllowsActiveUser() {
    User user = User.builder().build();

    assertThatCode(() -> checker.requireActive(user)).doesNotThrowAnyException();
  }

  @Test
  void requireActiveRejectsWithdrawnUserWithExistingErrorCode() {
    User user = User.builder().deletedAt(LocalDateTime.now()).build();

    assertThatThrownBy(() -> checker.requireActive(user))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode()).isEqualTo(AuthErrorCode.WITHDRAWAL_USER));
  }
}
