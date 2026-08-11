package com.example.demo.domain.artworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ArtworkFeelingReplyLikeRepository {

  Optional<ArtworkFeelingReplyLikeSnapshot> likeAndGetSnapshot(Long feelingReplyId, Long userId);

  Optional<ArtworkFeelingReplyLikeSnapshot> deleteAndGetSnapshot(Long feelingReplyId, Long userId);

  Map<Long, Long> countByFeelingReplyIds(List<Long> feelingReplyIds);

  Set<Long> findLikedFeelingReplyIds(List<Long> feelingReplyIds, Long userId);

  record ArtworkFeelingReplyLikeSnapshot(
      Long feelingReplyId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
