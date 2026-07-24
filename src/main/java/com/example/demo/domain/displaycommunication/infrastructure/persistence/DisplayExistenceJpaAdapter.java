package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.repository.DisplayExistenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DisplayExistenceJpaAdapter implements DisplayExistenceRepository {

  private final DisplayExistenceJpaRepository repository;

  @Override
  public boolean existsById(Long displayId) {
    return repository.existsById(displayId);
  }
}
