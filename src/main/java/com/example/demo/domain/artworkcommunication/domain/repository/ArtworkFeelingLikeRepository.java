package com.example.demo.domain.artworkcommunication.domain.repository;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingLike;
import java.util.Optional;

public interface ArtworkFeelingLikeRepository {

  ArtworkFeelingLike save(ArtworkFeelingLike artworkFeelingLike);

  Optional<ArtworkFeelingLike> findByFeelingIdAndUserId(Long feelingId, Long userId);

  long countActiveByFeelingId(Long feelingId);
}
