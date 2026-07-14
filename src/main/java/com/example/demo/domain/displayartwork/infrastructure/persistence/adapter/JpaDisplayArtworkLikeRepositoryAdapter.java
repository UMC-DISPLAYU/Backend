package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkLikeRepository;
import com.example.demo.domain.displayartwork.infrastructure.persistence.SpringDataDisplayArtworkLikeJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDisplayArtworkLikeRepositoryAdapter implements DisplayArtworkLikeRepository {

  private final SpringDataDisplayArtworkLikeJpaRepository jpaRepository;

  public JpaDisplayArtworkLikeRepositoryAdapter(
      SpringDataDisplayArtworkLikeJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public long countByDisplayArtworkId(Long displayArtworkId) {
    return jpaRepository.countByDisplayArtworkId(displayArtworkId);
  }

  @Override
  public boolean existsByDisplayArtworkIdAndUserId(Long displayArtworkId, Long userId) {
    return jpaRepository.existsByDisplayArtworkIdAndUserId(displayArtworkId, userId);
  }
}
