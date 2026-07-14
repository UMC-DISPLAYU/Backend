package com.example.demo.domain.displayartwork.infrastructure.persistence;

import com.example.demo.domain.displayartwork.domain.entity.DisplayArtworkLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDisplayArtworkLikeJpaRepository
    extends JpaRepository<DisplayArtworkLike, Long> {

  Optional<DisplayArtworkLike> findByDisplayArtworkIdAndUserId(Long displayArtworkId, Long userId);

  long countByDisplayArtworkIdAndDeletedAtIsNull(Long displayArtworkId);

  boolean existsByDisplayArtworkIdAndUserIdAndDeletedAtIsNull(Long displayArtworkId, Long userId);
}
