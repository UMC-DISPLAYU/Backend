package com.example.demo.domain.personalartworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PersonalArtworkQuestionLikeRepository {

  Optional<PersonalArtworkQuestionLikeSnapshot> toggleAndGetSnapshot(
      Long personalQuestionId, Long userId);

  Map<Long, Long> countByPersonalQuestionIds(List<Long> personalQuestionIds);

  record PersonalArtworkQuestionLikeSnapshot(
      Long personalQuestionId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
