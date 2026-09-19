package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.result.UserRoleResult;
import com.example.demo.domain.user.application.usecase.GetUserRoleUseCase;
import com.example.demo.domain.user.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUserRoleService implements GetUserRoleUseCase {

  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public Optional<UserRoleResult> findActiveRole(Long userId) {
    return userRepository
        .findById(userId)
        .filter(user -> user.getDeletedAt() == null)
        .map(user -> new UserRoleResult(user.getId(), user.getRole()));
  }
}
