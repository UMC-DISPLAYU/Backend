package com.example.demo.domain.displayartwork.domain.repository;

import com.example.demo.domain.displayartwork.domain.entity.DisplayArtworkLike;
import java.util.Optional;

public interface DisplayArtworkLikeRepository {

  Optional<DisplayArtworkLike> findByDisplayArtworkIdAndUserId(Long displayArtworkId, Long userId);

  DisplayArtworkLike save(DisplayArtworkLike displayArtworkLike);

  int deleteByDisplayArtworkIdAndUserId(Long displayArtworkId, Long userId);

  long countByDisplayArtworkId(Long displayArtworkId);

  boolean existsByDisplayArtworkIdAndUserId(Long displayArtworkId, Long userId);
}
