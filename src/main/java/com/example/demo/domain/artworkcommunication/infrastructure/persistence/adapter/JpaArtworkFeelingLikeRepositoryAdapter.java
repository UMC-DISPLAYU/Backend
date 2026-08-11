package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingLikeRepository.ArtworkFeelingLikeSnapshot;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.SpringDataArtworkFeelingLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkFeelingLikeRepositoryAdapter implements ArtworkFeelingLikeRepository {

  private final SpringDataArtworkFeelingLikeJpaRepository artworkFeelingLikeJpaRepository;

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

  @Override
  public Map<Long, Long> countByFeelingIds(List<Long> feelingIds) {
    return artworkFeelingLikeJpaRepository.countByFeelingIds(feelingIds).stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
  }

  @Override
  public Set<Long> findLikedFeelingIds(List<Long> feelingIds, Long userId) {
    return Set.copyOf(artworkFeelingLikeJpaRepository.findLikedFeelingIds(feelingIds, userId));
  }
}
