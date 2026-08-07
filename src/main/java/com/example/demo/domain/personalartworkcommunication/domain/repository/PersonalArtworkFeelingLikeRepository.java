package com.example.demo.domain.personalartworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PersonalArtworkFeelingLikeRepository {

  Optional<PersonalArtworkFeelingLikeSnapshot> toggleAndGetSnapshot(
      Long personalFeelingId, Long userId);

  Map<Long, Long> countByPersonalFeelingIds(List<Long> personalFeelingIds);

  Set<Long> findLikedPersonalFeelingIds(List<Long> personalFeelingIds, Long userId);

  record PersonalArtworkFeelingLikeSnapshot(
      Long personalFeelingId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
