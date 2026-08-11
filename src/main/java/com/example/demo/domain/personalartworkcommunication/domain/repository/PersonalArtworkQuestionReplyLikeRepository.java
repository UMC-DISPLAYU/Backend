package com.example.demo.domain.personalartworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PersonalArtworkQuestionReplyLikeRepository {

  Optional<PersonalArtworkQuestionReplyLikeSnapshot> likeAndGetSnapshot(
      Long personalQuestionReplyId, Long userId);

  Optional<PersonalArtworkQuestionReplyLikeSnapshot> deleteAndGetSnapshot(
      Long personalQuestionReplyId, Long userId);

  Map<Long, Long> countByPersonalQuestionReplyIds(List<Long> personalQuestionReplyIds);

  Set<Long> findLikedPersonalQuestionReplyIds(List<Long> personalQuestionReplyIds, Long userId);

  record PersonalArtworkQuestionReplyLikeSnapshot(
      Long personalQuestionReplyId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
