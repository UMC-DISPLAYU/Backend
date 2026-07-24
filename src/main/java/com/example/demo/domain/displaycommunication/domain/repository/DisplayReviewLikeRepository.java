package com.example.demo.domain.displaycommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DisplayReviewLikeRepository {
  Optional<DisplayReviewLikeSnapshot> toggleAndGetSnapshot(Long displayReviewId, Long userId);

  record DisplayReviewLikeSnapshot(
      Long displayReviewId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
