package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository.UserProfile;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PersonalArtworkUserExistenceJpaAdapter implements UserExistenceRepository {

  private final SpringDataPersonalArtworkUserExistenceJpaRepository repository;

  @Override
  public boolean existsById(Long userId) {
    return repository.existsById(userId);
  }

  @Override
  public Optional<String> findNicknameById(Long userId) {
    return repository.findNicknameByUserId(userId);
  }

  @Override
  public Map<Long, String> findNicknamesByIds(Set<Long> userIds) {
    if (userIds.isEmpty()) {
      return Map.of();
    }
    return repository.findByUserIdIn(userIds).stream()
        .collect(
            Collectors.toMap(
                PersonalArtworkUserReferenceJpaEntity::getUserId,
                PersonalArtworkUserReferenceJpaEntity::getNickname));
  }

  @Override
  public Map<Long, UserProfile> findUserProfilesByIds(Set<Long> userIds) {
    if (userIds.isEmpty()) {
      return Map.of();
    }
    return repository.findByUserIdIn(userIds).stream()
        .collect(
            Collectors.toMap(
                PersonalArtworkUserReferenceJpaEntity::getUserId,
                user ->
                    new UserProfile(
                        user.getUserId(), user.getNickname(), user.getProfileImageUrl())));
  }
}
