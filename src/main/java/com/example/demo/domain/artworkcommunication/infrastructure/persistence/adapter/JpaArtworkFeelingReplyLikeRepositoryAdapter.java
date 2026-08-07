package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReplyLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyLikeRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkFeelingReplyLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkFeelingReplyLikeRepositoryAdapter
    implements ArtworkFeelingReplyLikeRepository {

  private final ArtworkFeelingReplyLikeJpaRepository repository;

  @Override
  public ArtworkFeelingReplyLike save(ArtworkFeelingReplyLike artworkFeelingReplyLike) {
    return repository.save(artworkFeelingReplyLike);
  }

  @Override
  public java.util.Optional<ArtworkFeelingReplyLike> findByFeelingReplyIdAndUserId(
      Long feelingReplyId, Long userId) {
    return repository.findByFeelingReplyIdAndUserId(feelingReplyId, userId);
  }

  @Override
  public long countByFeelingReplyIdAndDeletedAtIsNull(Long feelingReplyId) {
    return repository.countByFeelingReplyIdAndDeletedAtIsNull(feelingReplyId);
  }

  @Override
  public Map<Long, Long> countByFeelingReplyIds(List<Long> feelingReplyIds) {
    return repository.countByFeelingReplyIds(feelingReplyIds).stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
  }

  @Override
  public Set<Long> findLikedFeelingReplyIds(List<Long> feelingReplyIds, Long userId) {
    return Set.copyOf(repository.findLikedFeelingReplyIds(feelingReplyIds, userId));
  }
}
