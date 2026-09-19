package com.example.demo.domain.user.application.usecase;

import com.example.demo.domain.user.application.result.UserRoleResult;
import java.util.Optional;

public interface GetUserRoleUseCase {

  Optional<UserRoleResult> findActiveRole(Long userId);
}
