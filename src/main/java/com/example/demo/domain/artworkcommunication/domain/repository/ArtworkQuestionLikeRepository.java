package com.example.demo.domain.artworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ArtworkQuestionLikeRepository {

  Optional<ArtworkQuestionLikeSnapshot> likeAndGetSnapshot(Long questionId, Long userId);

  Optional<ArtworkQuestionLikeSnapshot> deleteAndGetSnapshot(Long questionId, Long userId);

  Map<Long, Long> countByQuestionIds(List<Long> questionIds);

  Set<Long> findLikedQuestionIds(List<Long> questionIds, Long userId);

  record ArtworkQuestionLikeSnapshot(
      Long questionId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
