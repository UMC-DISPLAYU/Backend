package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingLike;
import java.util.Optional;

public interface ArtworkFeelingLikeRepository {

  void toggle(Long feelingId, Long userId);

  Optional<ArtworkFeelingLike> findByFeelingIdAndUserId(Long feelingId, Long userId);

  long countActiveByFeelingId(Long feelingId);
}
