package com.example.demo.domain.displaycommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DisplayReviewReplyLikeRepository {

  Optional<DisplayReviewReplyLikeSnapshot> toggleAndGetSnapshot(
      Long displayReviewReplyId, Long userId);

  Map<Long, Long> countByDisplayReviewReplyIds(List<Long> displayReviewReplyIds);

  record DisplayReviewReplyLikeSnapshot(
      Long displayReviewReplyId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
