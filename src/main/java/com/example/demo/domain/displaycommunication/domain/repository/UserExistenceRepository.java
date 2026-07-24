package com.example.demo.domain.displaycommunication.domain.repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserExistenceRepository {
  boolean existsById(Long userId);

  Optional<String> findNicknameById(Long userId);

  Map<Long, UserInfo> findUsersByIds(Set<Long> userIds);

  record UserInfo(Long userId, String nickname, String profileImageUrl) {}
}
