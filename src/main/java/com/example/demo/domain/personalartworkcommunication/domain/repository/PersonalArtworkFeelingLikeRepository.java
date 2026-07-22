package com.example.demo.domain.personalartworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PersonalArtworkFeelingLikeRepository {

  Optional<PersonalArtworkFeelingLikeSnapshot> toggleAndGetSnapshot(
      Long personalFeelingId, Long userId);

  record PersonalArtworkFeelingLikeSnapshot(
      Long personalFeelingId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
