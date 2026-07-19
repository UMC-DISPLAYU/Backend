package com.example.demo.domain.archive.infrastructure.persistence.adapter;

import com.example.demo.domain.archive.domain.repository.ArchiveArtistProfileExistenceRepository;
import com.example.demo.domain.archive.infrastructure.persistence.ArchiveArtistProfileExistenceJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ArchiveArtistProfileExistenceJpaAdapter
    implements ArchiveArtistProfileExistenceRepository {

  private final ArchiveArtistProfileExistenceJpaRepository jpaRepository;

  public ArchiveArtistProfileExistenceJpaAdapter(
      ArchiveArtistProfileExistenceJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public boolean existsById(Long artistProfileId) {
    return jpaRepository.existsById(artistProfileId);
  }
}
