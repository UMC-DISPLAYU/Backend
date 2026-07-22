package com.example.demo.domain.user.domain.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void replacesNicknameWithUniqueTemporaryValueWhenWithdrawn() {
    User user = User.builder().nickname("woosy").build();
    LocalDateTime withdrawnAt = LocalDateTime.of(2026, 7, 20, 12, 0);

    user.withdraw(withdrawnAt);

    assertThat(user.getNickname())
        .startsWith("deleted_")
        .matches("deleted_[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    assertThat(user.getNickname()).isNotEqualTo("woosy");
    assertThat(user.getDeletedAt()).isEqualTo(withdrawnAt);
  }
}
