package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingLikeRepository.ArtworkFeelingLikeSnapshot;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkFeelingLikeJpaRepository;
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

  private final ArtworkFeelingLikeJpaRepository artworkFeelingLikeJpaRepository;

  @Override
  public Optional<ArtworkFeelingLikeSnapshot> likeAndGetSnapshot(Long feelingId, Long userId) {
    artworkFeelingLikeJpaRepository.lockByFeelingId(feelingId);
    artworkFeelingLikeJpaRepository.insertIfAbsent(feelingId, userId);

    long likeCount = artworkFeelingLikeJpaRepository.countByFeelingId(feelingId);
    return artworkFeelingLikeJpaRepository
        .findByFeelingIdAndUserId(feelingId, userId)
        .map(feelingLike -> toSnapshot(feelingLike, likeCount));
  }

  @Override
  public Optional<ArtworkFeelingLikeSnapshot> deleteAndGetSnapshot(Long feelingId, Long userId) {
    artworkFeelingLikeJpaRepository.lockByFeelingId(feelingId);
    int deleted = artworkFeelingLikeJpaRepository.deleteByFeelingIdAndUserId(feelingId, userId);
    if (deleted == 0) {
      return Optional.empty();
    }
    long likeCount = artworkFeelingLikeJpaRepository.countByFeelingId(feelingId);
    return Optional.of(new ArtworkFeelingLikeSnapshot(feelingId, false, likeCount, null, null));
  }

  private ArtworkFeelingLikeSnapshot toSnapshot(ArtworkFeelingLike feelingLike, long likeCount) {
    return new ArtworkFeelingLikeSnapshot(
        feelingLike.getFeelingId(), true, likeCount, feelingLike.getCreatedAt(), null);
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
