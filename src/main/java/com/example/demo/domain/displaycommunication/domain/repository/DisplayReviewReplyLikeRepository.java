package com.example.demo.domain.displaycommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DisplayReviewReplyLikeRepository {

  Optional<DisplayReviewReplyLikeSnapshot> likeAndGetSnapshot(
      Long displayReviewReplyId, Long userId);

  Optional<DisplayReviewReplyLikeSnapshot> deleteAndGetSnapshot(
      Long displayReviewReplyId, Long userId);

  Map<Long, Long> countByDisplayReviewReplyIds(List<Long> displayReviewReplyIds);

  Set<Long> findLikedDisplayReviewReplyIds(List<Long> displayReviewReplyIds, Long userId);

  record DisplayReviewReplyLikeSnapshot(
      Long displayReviewReplyId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
