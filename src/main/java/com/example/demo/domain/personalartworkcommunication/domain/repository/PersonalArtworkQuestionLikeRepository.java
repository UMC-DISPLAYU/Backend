package com.example.demo.domain.personalartworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PersonalArtworkQuestionLikeRepository {

  Optional<PersonalArtworkQuestionLikeSnapshot> likeAndGetSnapshot(
      Long personalQuestionId, Long userId);

  Optional<PersonalArtworkQuestionLikeSnapshot> deleteAndGetSnapshot(
      Long personalQuestionId, Long userId);

  Map<Long, Long> countByPersonalQuestionIds(List<Long> personalQuestionIds);

  Set<Long> findLikedPersonalQuestionIds(List<Long> personalQuestionIds, Long userId);

  record PersonalArtworkQuestionLikeSnapshot(
      Long personalQuestionId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
