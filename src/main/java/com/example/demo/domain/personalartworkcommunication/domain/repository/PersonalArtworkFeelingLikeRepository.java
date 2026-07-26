package com.example.demo.domain.personalartworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PersonalArtworkFeelingLikeRepository {

  Optional<PersonalArtworkFeelingLikeSnapshot> toggleAndGetSnapshot(
      Long personalFeelingId, Long userId);

  Map<Long, Long> countByPersonalFeelingIds(List<Long> personalFeelingIds);

  record PersonalArtworkFeelingLikeSnapshot(
      Long personalFeelingId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
