package com.example.demo.domain.artworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ArtworkFeelingReplyLikeRepository {

  Optional<ArtworkFeelingReplyLikeSnapshot> toggleAndGetSnapshot(Long feelingReplyId, Long userId);

  record ArtworkFeelingReplyLikeSnapshot(
      Long feelingReplyId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
