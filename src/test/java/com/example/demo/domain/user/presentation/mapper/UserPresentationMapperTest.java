package com.example.demo.domain.user.presentation.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.user.application.result.MyUserResult;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.UserRole;
import com.example.demo.domain.user.presentation.response.MyUserResponse;
import org.junit.jupiter.api.Test;

class UserPresentationMapperTest {

  private final UserPresentationMapper mapper = new UserPresentationMapper();

  @Test
  void mapsRoleToMyUserResponse() {
    MyUserResult result =
        new MyUserResult(
            1L,
            Provider.Google,
            "Admin",
            "admin",
            null,
            UserRole.ADMIN,
            false,
            "admin@example.com",
            null);

    MyUserResponse response = mapper.toResponse(result);

    assertThat(response.role()).isEqualTo("ADMIN");
  }
}
