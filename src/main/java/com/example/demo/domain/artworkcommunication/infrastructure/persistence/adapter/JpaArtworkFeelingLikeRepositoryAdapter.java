package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingLikeRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkFeelingLikeJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkFeelingLikeRepositoryAdapter implements ArtworkFeelingLikeRepository {

  private final ArtworkFeelingLikeJpaRepository artworkFeelingLikeJpaRepository;

  @Override
  public ArtworkFeelingLike save(ArtworkFeelingLike artworkFeelingLike) {
    return artworkFeelingLikeJpaRepository.save(artworkFeelingLike);
  }

  @Override
  public Optional<ArtworkFeelingLike> findByFeelingIdAndUserId(Long feelingId, Long userId) {
    return artworkFeelingLikeJpaRepository.findByFeelingIdAndUserId(feelingId, userId);
  }

  @Override
  public long countActiveByFeelingId(Long feelingId) {
    return artworkFeelingLikeJpaRepository.countByFeelingIdAndDeletedAtIsNull(feelingId);
  }
}
