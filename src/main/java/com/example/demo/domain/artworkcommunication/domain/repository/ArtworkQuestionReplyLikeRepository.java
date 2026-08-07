package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReplyLike;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ArtworkQuestionReplyLikeRepository {

  ArtworkQuestionReplyLike save(ArtworkQuestionReplyLike artworkQuestionReplyLike);

  Optional<ArtworkQuestionReplyLike> findByQuestionReplyIdAndUserId(
      Long questionReplyId, Long userId);

  long countByQuestionReplyIdAndDeletedAtIsNull(Long questionReplyId);

  Map<Long, Long> countByQuestionReplyIds(List<Long> questionReplyIds);
}
