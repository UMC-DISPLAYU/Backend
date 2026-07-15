package com.example.demo.domain.artworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ArtworkFeelingLikeRepository {

  Optional<ArtworkFeelingLikeSnapshot> toggleAndGetSnapshot(Long feelingId, Long userId);

  record ArtworkFeelingLikeSnapshot(
      Long feelingId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
