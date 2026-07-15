package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingLikeRepository.ArtworkFeelingLikeSnapshot;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkFeelingLikeJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkFeelingLikeRepositoryAdapter implements ArtworkFeelingLikeRepository {

  private final ArtworkFeelingLikeJpaRepository artworkFeelingLikeJpaRepository;

  @Override
  public Optional<ArtworkFeelingLikeSnapshot> toggleAndGetSnapshot(Long feelingId, Long userId) {
    artworkFeelingLikeJpaRepository.lockByFeelingId(feelingId);
    artworkFeelingLikeJpaRepository.toggle(feelingId, userId);

    long likeCount = artworkFeelingLikeJpaRepository.countByFeelingIdAndDeletedAtIsNull(feelingId);
    return artworkFeelingLikeJpaRepository
        .findByFeelingIdAndUserId(feelingId, userId)
        .map(feelingLike -> toSnapshot(feelingLike, likeCount));
  }

  private ArtworkFeelingLikeSnapshot toSnapshot(ArtworkFeelingLike feelingLike, long likeCount) {
    return new ArtworkFeelingLikeSnapshot(
        feelingLike.getFeelingId(),
        !feelingLike.isDeleted(),
        likeCount,
        feelingLike.getCreatedAt(),
        feelingLike.getDeletedAt());
  }
}
