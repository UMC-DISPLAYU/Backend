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
  public long countByDisplayArtworkIdAndDeletedAtIsNull(Long displayArtworkId) {
    return jpaRepository.countByDisplayArtworkIdAndDeletedAtIsNull(displayArtworkId);
  }

  @Override
  public boolean existsByDisplayArtworkIdAndUserIdAndDeletedAtIsNull(
      Long displayArtworkId, Long userId) {
    return jpaRepository.existsByDisplayArtworkIdAndUserIdAndDeletedAtIsNull(
        displayArtworkId, userId);
  }
}
