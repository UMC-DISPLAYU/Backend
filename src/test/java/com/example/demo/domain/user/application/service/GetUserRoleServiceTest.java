package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.application.result.UserRoleResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.type.UserRole;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GetUserRoleServiceTest {

  private static final Long USER_ID = 1L;
  private final UserRepository userRepository = mock(UserRepository.class);
  private final GetUserRoleService service = new GetUserRoleService(userRepository);

  @Test
  void returnsRoleForActiveUser() {
    User user = User.builder().id(USER_ID).role(UserRole.ADMIN).build();
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    Optional<UserRoleResult> result = service.findActiveRole(USER_ID);

    assertThat(result).contains(new UserRoleResult(USER_ID, UserRole.ADMIN));
  }

  @Test
  void returnsEmptyForWithdrawnUser() {
    User user =
        User.builder()
            .id(USER_ID)
            .role(UserRole.ADMIN)
            .deletedAt(LocalDateTime.parse("2026-09-19T12:00:00"))
            .build();
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    Optional<UserRoleResult> result = service.findActiveRole(USER_ID);

    assertThat(result).isEmpty();
  }

  @Test
  void returnsEmptyForMissingUser() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

    Optional<UserRoleResult> result = service.findActiveRole(USER_ID);

    assertThat(result).isEmpty();
  }
}
