package com.example.demo.domain.displaycommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DisplayReviewLikeRepository {
  Optional<DisplayReviewLikeSnapshot> likeAndGetSnapshot(Long displayReviewId, Long userId);

  Optional<DisplayReviewLikeSnapshot> deleteAndGetSnapshot(Long displayReviewId, Long userId);

  Map<Long, Long> countByDisplayReviewIds(List<Long> displayReviewIds);

  Set<Long> findLikedDisplayReviewIds(List<Long> displayReviewIds, Long userId);

  record DisplayReviewLikeSnapshot(
      Long displayReviewId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
