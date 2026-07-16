package com.example.demo.domain.archive.infrastructure.persistence.adapter;

import com.example.demo.domain.archive.domain.repository.ArchiveCreatorExistenceRepository;
import com.example.demo.domain.archive.infrastructure.persistence.ArchiveCreatorExistenceJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ArchiveCreatorExistenceJpaAdapter implements ArchiveCreatorExistenceRepository {

  private final ArchiveCreatorExistenceJpaRepository jpaRepository;

  public ArchiveCreatorExistenceJpaAdapter(ArchiveCreatorExistenceJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public boolean existsById(Long creatorId) {
    return jpaRepository.existsById(creatorId);
  }
}
