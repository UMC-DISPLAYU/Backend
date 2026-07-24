package com.example.demo.domain.displaycommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DisplayReviewReplyLikeRepository {

  Optional<DisplayReviewReplyLikeSnapshot> toggleAndGetSnapshot(
      Long displayReviewReplyId, Long userId);

  record DisplayReviewReplyLikeSnapshot(
      Long displayReviewReplyId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
