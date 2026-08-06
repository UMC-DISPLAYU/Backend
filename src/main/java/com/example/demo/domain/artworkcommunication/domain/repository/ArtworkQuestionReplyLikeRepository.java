package com.example.demo.domain.artworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ArtworkQuestionReplyLikeRepository {

  Optional<ArtworkQuestionReplyLikeSnapshot> toggleAndGetSnapshot(
      Long questionReplyId, Long userId);

  Map<Long, Long> countByQuestionReplyIds(List<Long> questionReplyIds);

  record ArtworkQuestionReplyLikeSnapshot(
      Long questionReplyId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
