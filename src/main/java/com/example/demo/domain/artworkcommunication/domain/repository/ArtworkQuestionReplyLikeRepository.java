package com.example.demo.domain.artworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ArtworkQuestionReplyLikeRepository {

  Optional<ArtworkQuestionReplyLikeSnapshot> toggleAndGetSnapshot(
      Long questionReplyId, Long userId);

  record ArtworkQuestionReplyLikeSnapshot(
      Long questionReplyId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
