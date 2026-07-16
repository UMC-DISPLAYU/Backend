package com.example.demo.domain.archive.infrastructure.persistence.adapter;

import com.example.demo.domain.archive.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.archive.infrastructure.persistence.CreatorExistenceJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CreatorExistenceJpaAdapter implements CreatorExistenceRepository {

  private final CreatorExistenceJpaRepository jpaRepository;

  public CreatorExistenceJpaAdapter(CreatorExistenceJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public boolean existsById(Long creatorId) {
    return jpaRepository.existsById(creatorId);
  }
}
