package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingLikeRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkFeelingLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
  public java.util.Optional<ArtworkFeelingLike> findByFeelingIdAndUserId(
      Long feelingId, Long userId) {
    return artworkFeelingLikeJpaRepository.findByFeelingIdAndUserId(feelingId, userId);
  }

  @Override
  public long countByFeelingIdAndDeletedAtIsNull(Long feelingId) {
    return artworkFeelingLikeJpaRepository.countByFeelingIdAndDeletedAtIsNull(feelingId);
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
