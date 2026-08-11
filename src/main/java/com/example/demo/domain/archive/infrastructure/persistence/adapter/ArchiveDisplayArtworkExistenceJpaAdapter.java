package com.example.demo.domain.archive.infrastructure.persistence.adapter;

import com.example.demo.domain.archive.domain.repository.ArchiveDisplayArtworkExistenceRepository;
import com.example.demo.domain.archive.infrastructure.persistence.SpringDataArchiveDisplayArtworkExistenceJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ArchiveDisplayArtworkExistenceJpaAdapter
    implements ArchiveDisplayArtworkExistenceRepository {

  private final SpringDataArchiveDisplayArtworkExistenceJpaRepository jpaRepository;

  public ArchiveDisplayArtworkExistenceJpaAdapter(
      SpringDataArchiveDisplayArtworkExistenceJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public boolean existsById(Long displayArtworkId) {
    return jpaRepository.existsById(displayArtworkId);
  }
}
