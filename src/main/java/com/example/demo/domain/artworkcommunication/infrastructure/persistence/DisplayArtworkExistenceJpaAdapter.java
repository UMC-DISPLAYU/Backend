package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DisplayArtworkExistenceJpaAdapter implements DisplayArtworkExistenceRepository {

  private final DisplayArtworkExistenceJpaRepository repository;

  @Override
  public boolean existsById(Long displayArtworkId) {
    return repository.existsById(displayArtworkId);
  }
}
