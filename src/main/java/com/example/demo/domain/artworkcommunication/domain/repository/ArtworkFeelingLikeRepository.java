package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingLike;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ArtworkFeelingLikeRepository {

  ArtworkFeelingLike save(ArtworkFeelingLike artworkFeelingLike);

  Optional<ArtworkFeelingLike> findByFeelingIdAndUserId(Long feelingId, Long userId);

  long countByFeelingIdAndDeletedAtIsNull(Long feelingId);

  Map<Long, Long> countByFeelingIds(List<Long> feelingIds);

  Set<Long> findLikedFeelingIds(List<Long> feelingIds, Long userId);
}
