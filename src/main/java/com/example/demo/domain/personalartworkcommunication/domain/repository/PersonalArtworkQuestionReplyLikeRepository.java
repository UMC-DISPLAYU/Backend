package com.example.demo.domain.personalartworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PersonalArtworkQuestionReplyLikeRepository {

  Optional<PersonalArtworkQuestionReplyLikeSnapshot> toggleAndGetSnapshot(
      Long personalQuestionReplyId, Long userId);

  Map<Long, Long> countByPersonalQuestionReplyIds(List<Long> personalQuestionReplyIds);

  record PersonalArtworkQuestionReplyLikeSnapshot(
      Long personalQuestionReplyId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
