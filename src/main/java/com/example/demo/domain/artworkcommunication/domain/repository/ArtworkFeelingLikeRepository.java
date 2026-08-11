package com.example.demo.domain.artworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ArtworkFeelingLikeRepository {

  Optional<ArtworkFeelingLikeSnapshot> likeAndGetSnapshot(Long feelingId, Long userId);

  Optional<ArtworkFeelingLikeSnapshot> deleteAndGetSnapshot(Long feelingId, Long userId);

  Map<Long, Long> countByFeelingIds(List<Long> feelingIds);

  Set<Long> findLikedFeelingIds(List<Long> feelingIds, Long userId);

  record ArtworkFeelingLikeSnapshot(
      Long feelingId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
