package com.example.demo.domain.admin.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.example.demo.domain.user.application.result.UserRoleResult;
import com.example.demo.domain.user.application.usecase.GetUserRoleUseCase;
import com.example.demo.domain.user.domain.type.UserRole;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AdminAccessAdapterTest {
  @Test
  void checksCurrentActiveRoleOnEveryCall() {
    GetUserRoleUseCase roles = mock(GetUserRoleUseCase.class);
    AdminAccessAdapter adapter = new AdminAccessAdapter(roles);
    when(roles.findActiveRole(1L))
        .thenReturn(
            Optional.of(new UserRoleResult(1L, UserRole.ADMIN)),
            Optional.of(new UserRoleResult(1L, UserRole.USER)),
            Optional.empty());
    assertThat(adapter.isAdmin(1L)).isTrue();
    assertThat(adapter.isAdmin(1L)).isFalse();
    assertThat(adapter.isAdmin(1L)).isFalse();
    verify(roles, times(3)).findActiveRole(1L);
  }
}
