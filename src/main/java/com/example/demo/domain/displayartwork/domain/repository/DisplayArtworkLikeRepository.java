package com.example.demo.domain.displayartwork.domain.repository;

import com.example.demo.domain.displayartwork.domain.entity.DisplayArtworkLike;
import java.util.Optional;

public interface DisplayArtworkLikeRepository {

  Optional<DisplayArtworkLike> findByDisplayArtworkIdAndUserId(Long displayArtworkId, Long userId);

  DisplayArtworkLike save(DisplayArtworkLike displayArtworkLike);

  long countByDisplayArtworkIdAndDeletedAtIsNull(Long displayArtworkId);

  boolean existsByDisplayArtworkIdAndUserIdAndDeletedAtIsNull(Long displayArtworkId, Long userId);
}
