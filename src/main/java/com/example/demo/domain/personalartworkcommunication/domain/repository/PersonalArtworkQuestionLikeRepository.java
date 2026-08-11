package com.example.demo.domain.personalartworkcommunication.domain.repository;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionLike;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PersonalArtworkQuestionLikeRepository {

  PersonalArtworkQuestionLike save(PersonalArtworkQuestionLike personalArtworkQuestionLike);

  Optional<PersonalArtworkQuestionLike> findByPersonalQuestionIdAndUserId(
      Long personalQuestionId, Long userId);

  long countByPersonalQuestionIdAndDeletedAtIsNull(Long personalQuestionId);

  Map<Long, Long> countByPersonalQuestionIds(List<Long> personalQuestionIds);
  Set<Long> findLikedPersonalQuestionIds(List<Long> personalQuestionIds, Long userId);

  record PersonalArtworkQuestionLikeSnapshot(
      Long personalQuestionId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
