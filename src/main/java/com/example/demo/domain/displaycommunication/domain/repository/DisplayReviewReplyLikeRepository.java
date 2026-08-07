package com.example.demo.domain.displaycommunication.domain.repository;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReplyLike;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DisplayReviewReplyLikeRepository {

  DisplayReviewReplyLike save(DisplayReviewReplyLike displayReviewReplyLike);

  Optional<DisplayReviewReplyLike> findByDisplayReviewReplyIdAndUserId(
      Long displayReviewReplyId, Long userId);

  long countByDisplayReviewReplyIdAndDeletedAtIsNull(Long displayReviewReplyId);

  Map<Long, Long> countByDisplayReviewReplyIds(List<Long> displayReviewReplyIds);

  Set<Long> findLikedDisplayReviewReplyIds(List<Long> displayReviewReplyIds, Long userId);
}
