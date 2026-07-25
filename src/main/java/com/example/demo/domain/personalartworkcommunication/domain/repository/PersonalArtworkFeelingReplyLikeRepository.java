package com.example.demo.domain.personalartworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PersonalArtworkFeelingReplyLikeRepository {

  Optional<PersonalArtworkFeelingReplyLikeSnapshot> toggleAndGetSnapshot(
      Long personalFeelingReplyId, Long userId);

  record PersonalArtworkFeelingReplyLikeSnapshot(
      Long personalFeelingReplyId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
