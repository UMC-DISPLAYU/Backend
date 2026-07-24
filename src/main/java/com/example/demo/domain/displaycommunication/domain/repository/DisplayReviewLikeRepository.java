package com.example.demo.domain.displaycommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DisplayReviewLikeRepository {
  Optional<DisplayReviewLikeSnapshot> toggleAndGetSnapshot(Long displayReviewId, Long userId);

  Map<Long, Long> countByDisplayReviewIds(List<Long> displayReviewIds);

  record DisplayReviewLikeSnapshot(
      Long displayReviewId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
