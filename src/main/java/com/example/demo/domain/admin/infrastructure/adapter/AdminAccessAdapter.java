package com.example.demo.domain.admin.infrastructure.adapter;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.user.application.usecase.GetUserRoleUseCase;
import com.example.demo.domain.user.domain.type.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAccessAdapter implements AdminAccessPort {
  private final GetUserRoleUseCase getUserRoleUseCase;

  @Override
  public boolean isAdmin(Long userId) {
    return getUserRoleUseCase
        .findActiveRole(userId)
        .map(result -> result.role() == UserRole.ADMIN)
        .orElse(false);
  }
}
