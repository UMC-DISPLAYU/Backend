package com.example.demo.domain.displayartwork.domain.repository;

public interface DisplayArtworkLikeRepository {

  long countByDisplayArtworkId(Long displayArtworkId);

  boolean existsByDisplayArtworkIdAndUserId(Long displayArtworkId, Long userId);
}
