package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import java.util.Optional;
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
}
