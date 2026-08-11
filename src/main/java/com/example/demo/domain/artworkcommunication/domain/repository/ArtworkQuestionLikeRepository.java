package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionLike;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ArtworkQuestionLikeRepository {

  ArtworkQuestionLike save(ArtworkQuestionLike artworkQuestionLike);

  Optional<ArtworkQuestionLike> findByQuestionIdAndUserId(Long questionId, Long userId);

  long countByQuestionIdAndDeletedAtIsNull(Long questionId);

  Map<Long, Long> countByQuestionIds(List<Long> questionIds);
  Set<Long> findLikedQuestionIds(List<Long> questionIds, Long userId);

  record ArtworkQuestionLikeSnapshot(
      Long questionId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
