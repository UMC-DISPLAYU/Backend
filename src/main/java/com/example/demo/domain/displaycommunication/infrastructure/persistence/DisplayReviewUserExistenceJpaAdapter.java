package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.repository.UserExistenceRepository;
import com.example.demo.domain.displaycommunication.domain.repository.UserExistenceRepository.UserInfo;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DisplayReviewUserExistenceJpaAdapter implements UserExistenceRepository {
  private final SpringDataDisplayReviewUserExistenceJpaRepository repository;
  private final DisplayReviewPersistenceMapper mapper;

  @Override
  public boolean existsById(Long userId) {
    return repository.existsByUserIdAndDeletedAtIsNull(userId);
  }

  @Override
  public Optional<String> findNicknameById(Long userId) {
    return repository
        .findByUserIdAndDeletedAtIsNull(userId)
        .map(DisplayReviewUserReferenceJpaEntity::getNickname);
  }

  @Override
  public Map<Long, UserInfo> findUsersByIds(Set<Long> userIds) {
    if (userIds.isEmpty()) {
      return Map.of();
    }
    return repository.findByUserIdInAndDeletedAtIsNull(userIds).stream()
        .collect(
            Collectors.toMap(DisplayReviewUserReferenceJpaEntity::getUserId, mapper::toUserInfo));
  }
}
