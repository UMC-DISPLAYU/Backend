package com.example.demo.domain.archive.infrastructure.persistence.adapter;

import com.example.demo.domain.archive.domain.repository.ArchiveDisplayExistenceRepository;
import com.example.demo.domain.archive.infrastructure.persistence.SpringDataArchiveDisplayExistenceJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ArchiveDisplayExistenceJpaAdapter implements ArchiveDisplayExistenceRepository {

  private final SpringDataArchiveDisplayExistenceJpaRepository jpaRepository;

  public ArchiveDisplayExistenceJpaAdapter(
      SpringDataArchiveDisplayExistenceJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public boolean existsById(Long displayId) {
    return jpaRepository.existsById(displayId);
  }
}
