package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.displaycommunication.domain.repository.UserExistenceRepository.UserInfo;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DisplayReviewPersistenceMapperTest {

  private final DisplayReviewPersistenceMapper mapper = new DisplayReviewPersistenceMapper();

  @Test
  void withdrawnUserIsAnonymizedRegardlessOfStoredProfileValues() {
    DisplayReviewUserReferenceJpaEntity user = userReference();
    ReflectionTestUtils.setField(user, "userId", 1L);
    ReflectionTestUtils.setField(user, "nickname", "550e8400-e29b-41d4-a716-446655440000");
    ReflectionTestUtils.setField(user, "profileImageUrl", "https://example.com/old-profile.jpg");
    ReflectionTestUtils.setField(user, "deletedAt", LocalDateTime.of(2025, 1, 2, 3, 4));

    UserInfo result = mapper.toUserInfo(user);

    assertThat(result.userId()).isEqualTo(1L);
    assertThat(result.nickname()).isEqualTo("탈퇴한 회원");
    assertThat(result.profileImageUrl()).isNull();
  }

  @Test
  void activeUserKeepsStoredProfileValues() {
    DisplayReviewUserReferenceJpaEntity user = userReference();
    ReflectionTestUtils.setField(user, "userId", 2L);
    ReflectionTestUtils.setField(user, "nickname", "김디유");
    ReflectionTestUtils.setField(user, "profileImageUrl", "https://example.com/profile.jpg");

    UserInfo result = mapper.toUserInfo(user);

    assertThat(result.userId()).isEqualTo(2L);
    assertThat(result.nickname()).isEqualTo("김디유");
    assertThat(result.profileImageUrl()).isEqualTo("https://example.com/profile.jpg");
  }

  private static DisplayReviewUserReferenceJpaEntity userReference() {
    return new DisplayReviewUserReferenceJpaEntity();
  }
}
