package com.example.demo.domain.artworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ArtworkQuestionLikeRepository {

  Optional<ArtworkQuestionLikeSnapshot> toggleAndGetSnapshot(Long questionId, Long userId);

  record ArtworkQuestionLikeSnapshot(
      Long questionId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
