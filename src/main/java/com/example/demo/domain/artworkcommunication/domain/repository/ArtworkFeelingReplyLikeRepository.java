package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReplyLike;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ArtworkFeelingReplyLikeRepository {

  ArtworkFeelingReplyLike save(ArtworkFeelingReplyLike artworkFeelingReplyLike);

  Optional<ArtworkFeelingReplyLike> findByFeelingReplyIdAndUserId(Long feelingReplyId, Long userId);

  long countByFeelingReplyIdAndDeletedAtIsNull(Long feelingReplyId);

  Map<Long, Long> countByFeelingReplyIds(List<Long> feelingReplyIds);

  Set<Long> findLikedFeelingReplyIds(List<Long> feelingReplyIds, Long userId);
}
