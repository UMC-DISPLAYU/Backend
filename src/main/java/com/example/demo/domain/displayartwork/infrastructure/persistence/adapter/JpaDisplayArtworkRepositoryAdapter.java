package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.domain.displayartwork.infrastructure.persistence.SpringDataDisplayArtworkJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDisplayArtworkRepositoryAdapter implements DisplayArtworkRepository {

  private final SpringDataDisplayArtworkJpaRepository jpaRepository;

  public JpaDisplayArtworkRepositoryAdapter(SpringDataDisplayArtworkJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<DisplayArtwork> findById(Long displayArtworkId) {
    return jpaRepository.findById(displayArtworkId);
  }

  @Override
  public int countByDisplayId(Long displayId) {
    return jpaRepository.countByDisplayId(displayId);
  }

  @Override
  public DisplayArtwork save(DisplayArtwork displayArtwork) {
    return jpaRepository.save(displayArtwork);
  }
}
