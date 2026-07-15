package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserExistenceJpaAdapter implements UserExistenceRepository {

  private final UserExistenceJpaRepository repository;

  @Override
  public boolean existsById(Long userId) {
    return repository.existsById(userId);
  }

  @Override
  public Optional<String> findNicknameById(Long userId) {
    return repository.findNicknameById(userId);
  }

  @Override
  public Map<Long, String> findNicknamesByIds(Set<Long> userIds) {
    if (userIds.isEmpty()) {
      return Map.of();
    }

    return repository.findByUserIdIn(userIds).stream()
        .collect(
            Collectors.toMap(
                UserReferenceJpaEntity::getUserId, UserReferenceJpaEntity::getNickname));
  }
}
