package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PersonalArtworkUserExistenceJpaAdapter implements UserExistenceRepository {

  private final PersonalArtworkUserExistenceJpaRepository repository;

  @Override
  public boolean existsById(Long userId) {
    return repository.existsById(userId);
  }
}
