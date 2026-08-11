package com.example.demo.domain.artworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ArtworkQuestionReplyLikeRepository {

  Optional<ArtworkQuestionReplyLikeSnapshot> likeAndGetSnapshot(Long questionReplyId, Long userId);

  Optional<ArtworkQuestionReplyLikeSnapshot> deleteAndGetSnapshot(
      Long questionReplyId, Long userId);

  Map<Long, Long> countByQuestionReplyIds(List<Long> questionReplyIds);

  Set<Long> findLikedQuestionReplyIds(List<Long> questionReplyIds, Long userId);

  record ArtworkQuestionReplyLikeSnapshot(
      Long questionReplyId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
