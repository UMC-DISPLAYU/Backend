package com.example.demo.domain.personalartworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PersonalArtworkQuestionReplyLikeRepository {

  Optional<PersonalArtworkQuestionReplyLikeSnapshot> toggleAndGetSnapshot(
      Long personalQuestionReplyId, Long userId);

  record PersonalArtworkQuestionReplyLikeSnapshot(
      Long personalQuestionReplyId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
