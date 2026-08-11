package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReplyLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyLikeRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.SpringDataArtworkFeelingReplyLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkFeelingReplyLikeRepositoryAdapter
    implements ArtworkFeelingReplyLikeRepository {

  private final SpringDataArtworkFeelingReplyLikeJpaRepository repository;

  @Override
  public Optional<ArtworkFeelingReplyLikeSnapshot> likeAndGetSnapshot(
      Long feelingReplyId, Long userId) {
    repository.insertIfAbsent(feelingReplyId, userId);

    long likeCount = repository.countByFeelingReplyId(feelingReplyId);
    return repository
        .findByFeelingReplyIdAndUserId(feelingReplyId, userId)
        .map(replyLike -> toSnapshot(replyLike, likeCount));
  }

  @Override
  public Optional<ArtworkFeelingReplyLikeSnapshot> deleteAndGetSnapshot(
      Long feelingReplyId, Long userId) {
    int deleted = repository.deleteByFeelingReplyIdAndUserId(feelingReplyId, userId);
    if (deleted == 0) {
      return Optional.empty();
    }
    long likeCount = repository.countByFeelingReplyId(feelingReplyId);
    return Optional.of(
        new ArtworkFeelingReplyLikeSnapshot(feelingReplyId, false, likeCount, null, null));
  }

  private ArtworkFeelingReplyLikeSnapshot toSnapshot(
      ArtworkFeelingReplyLike replyLike, long likeCount) {
    return new ArtworkFeelingReplyLikeSnapshot(
        replyLike.getFeelingReplyId(), true, likeCount, replyLike.getCreatedAt(), null);
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
