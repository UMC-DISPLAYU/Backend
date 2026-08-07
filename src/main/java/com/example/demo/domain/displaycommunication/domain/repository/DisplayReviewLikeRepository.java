package com.example.demo.domain.displaycommunication.domain.repository;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewLike;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DisplayReviewLikeRepository {
  DisplayReviewLike save(DisplayReviewLike displayReviewLike);

  Optional<DisplayReviewLike> findByDisplayReviewIdAndUserId(Long displayReviewId, Long userId);

  long countByDisplayReviewIdAndDeletedAtIsNull(Long displayReviewId);

  Map<Long, Long> countByDisplayReviewIds(List<Long> displayReviewIds);

  Set<Long> findLikedDisplayReviewIds(List<Long> displayReviewIds, Long userId);
}
