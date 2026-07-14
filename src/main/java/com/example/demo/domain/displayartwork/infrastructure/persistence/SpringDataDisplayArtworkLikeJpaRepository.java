package com.example.demo.domain.displayartwork.infrastructure.persistence;

import com.example.demo.domain.displayartwork.domain.entity.DisplayArtworkLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDisplayArtworkLikeJpaRepository
    extends JpaRepository<DisplayArtworkLike, Long> {

  long countByDisplayArtworkId(Long displayArtworkId);

  boolean existsByDisplayArtworkIdAndUserId(Long displayArtworkId, Long userId);
}
