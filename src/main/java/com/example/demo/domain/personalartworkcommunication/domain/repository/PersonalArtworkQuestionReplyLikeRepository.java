package com.example.demo.domain.personalartworkcommunication.domain.repository;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReplyLike;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PersonalArtworkQuestionReplyLikeRepository {

  PersonalArtworkQuestionReplyLike save(
      PersonalArtworkQuestionReplyLike personalArtworkQuestionReplyLike);

  Optional<PersonalArtworkQuestionReplyLike> findByPersonalQuestionReplyIdAndUserId(
      Long personalQuestionReplyId, Long userId);

  long countByPersonalQuestionReplyIdAndDeletedAtIsNull(Long personalQuestionReplyId);

  Map<Long, Long> countByPersonalQuestionReplyIds(List<Long> personalQuestionReplyIds);
  Set<Long> findLikedPersonalQuestionReplyIds(List<Long> personalQuestionReplyIds, Long userId);

  record PersonalArtworkQuestionReplyLikeSnapshot(
      Long personalQuestionReplyId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
