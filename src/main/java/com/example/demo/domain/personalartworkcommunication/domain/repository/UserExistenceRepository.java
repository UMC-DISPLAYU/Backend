package com.example.demo.domain.personalartworkcommunication.domain.repository;

import java.util.Optional;

public interface UserExistenceRepository {
  boolean existsById(Long userId);

  Optional<String> findNicknameById(Long userId);
}
