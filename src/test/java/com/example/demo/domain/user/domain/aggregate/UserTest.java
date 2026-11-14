package com.example.demo.domain.user.domain.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.user.domain.type.UserRole;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void defaultsRoleToUser() {
    User user = User.builder().build();

    assertThat(user.getRole()).isEqualTo(UserRole.USER);
  }

  @Test
  void anonymizesPersonalAndSocialInformationWhenWithdrawn() {
    User user =
        User.builder()
            .providerId("social-id")
            .name("우아링")
            .nickname("woosy")
            .profileImageUrl("https://example.com/profile.jpg")
            .isVerified(true)
            .socialEmail("social@example.com")
            .schoolEmail("student@university.ac.kr")
            .univName("대학교")
            .role(UserRole.ADMIN)
            .build();
    LocalDateTime withdrawnAt = LocalDateTime.of(2026, 7, 20, 12, 0);

    user.withdraw(withdrawnAt);

    assertThat(user.getProviderId()).startsWith("withdrawn_");
    assertThat(user.getName()).isEqualTo(User.WITHDRAWN_DISPLAY_NAME);
    assertThat(user.getNickname()).isEqualTo(User.WITHDRAWN_DISPLAY_NAME);
    assertThat(user.getProfileImageUrl()).isNull();
    assertThat(user.isVerified()).isFalse();
    assertThat(user.getSocialEmail()).endsWith("@withdrawn.invalid");
    assertThat(user.getSchoolEmail()).isNull();
    assertThat(user.getUnivName()).isNull();
    assertThat(user.getUniversity()).isNull();
    assertThat(user.getRole()).isEqualTo(UserRole.USER);
    assertThat(user.getDeletedAt()).isEqualTo(withdrawnAt);
  }
}
