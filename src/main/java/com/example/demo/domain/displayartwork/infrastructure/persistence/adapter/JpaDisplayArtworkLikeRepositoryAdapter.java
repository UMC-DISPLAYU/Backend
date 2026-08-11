package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.displayartwork.domain.entity.DisplayArtworkLike;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkLikeRepository;
import com.example.demo.domain.displayartwork.infrastructure.persistence.SpringDataDisplayArtworkLikeJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDisplayArtworkLikeRepositoryAdapter implements DisplayArtworkLikeRepository {

  private final SpringDataDisplayArtworkLikeJpaRepository jpaRepository;

  public JpaDisplayArtworkLikeRepositoryAdapter(
      SpringDataDisplayArtworkLikeJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<DisplayArtworkLike> findByDisplayArtworkIdAndUserId(
      Long displayArtworkId, Long userId) {
    return jpaRepository.findByDisplayArtworkIdAndUserId(displayArtworkId, userId);
  }

  @Override
  public DisplayArtworkLike save(DisplayArtworkLike displayArtworkLike) {
    return jpaRepository.save(displayArtworkLike);
  }

  @Override
  public int deleteByDisplayArtworkIdAndUserId(Long displayArtworkId, Long userId) {
    return jpaRepository.deleteByDisplayArtworkIdAndUserId(displayArtworkId, userId);
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
