package com.example.demo.domain.user.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.user.domain.aggregate.User;
import org.junit.jupiter.api.Test;

class SchoolEmailVerificationTest {

  @Test
  void allowsResendExactlyOneMinuteAfterSent() {
    SchoolEmailVerification verification =
        SchoolEmailVerification.create(
            User.builder().build(), "student@example.com", "Example University", "123456");

    assertThat(verification.canResend(verification.getSentAt().plusMinutes(1))).isTrue();
  }
}
