package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.application.permission.UserPermissionChecker;
import com.example.demo.domain.user.application.result.MyUserResult;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.UserRole;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GetMyUserServiceTest {

  private static final Long USER_ID = 1L;
  private final UserRepository userRepository = mock(UserRepository.class);
  private final GetMyUserService service =
      new GetMyUserService(userRepository, new UserPermissionChecker());

  @Test
  void returnsRoleWithMyUserInformation() {
    User user =
        User.builder()
            .id(USER_ID)
            .provider(Provider.Google)
            .providerId("google-user")
            .role(UserRole.ADMIN)
            .name("Admin")
            .nickname("admin")
            .socialEmail("admin@example.com")
            .build();
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    MyUserResult result = service.execute(USER_ID);

    assertThat(result.role()).isEqualTo(UserRole.ADMIN);
  }
}
