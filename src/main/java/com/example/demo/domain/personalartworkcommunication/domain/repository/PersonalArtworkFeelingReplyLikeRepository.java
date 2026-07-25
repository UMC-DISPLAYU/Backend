package com.example.demo.domain.personalartworkcommunication.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PersonalArtworkFeelingReplyLikeRepository {

  Optional<PersonalArtworkFeelingReplyLikeSnapshot> toggleAndGetSnapshot(
      Long personalFeelingReplyId, Long userId);

  Map<Long, Long> countByPersonalFeelingReplyIds(List<Long> personalFeelingReplyIds);

  record PersonalArtworkFeelingReplyLikeSnapshot(
      Long personalFeelingReplyId,
      boolean liked,
      long likeCount,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {}
}
