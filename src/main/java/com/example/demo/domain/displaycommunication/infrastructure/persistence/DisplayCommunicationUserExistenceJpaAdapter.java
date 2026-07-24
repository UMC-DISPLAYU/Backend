package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.repository.UserExistenceRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DisplayCommunicationUserExistenceJpaAdapter implements UserExistenceRepository {
  private final DisplayCommunicationUserExistenceJpaRepository repository;

  @Override
  public boolean existsById(Long userId) {
    return repository.existsByUserIdAndDeletedAtIsNull(userId);
  }

  @Override
  public Optional<String> findNicknameById(Long userId) {
    return repository
        .findByUserIdAndDeletedAtIsNull(userId)
        .map(DisplayCommunicationUserReferenceJpaEntity::getNickname);
  }
}
