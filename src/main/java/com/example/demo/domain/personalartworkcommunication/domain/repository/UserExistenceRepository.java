package com.example.demo.domain.personalartworkcommunication.domain.repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserExistenceRepository {
  boolean existsById(Long userId);

  Optional<String> findNicknameById(Long userId);

  Map<Long, String> findNicknamesByIds(Set<Long> userIds);
}
