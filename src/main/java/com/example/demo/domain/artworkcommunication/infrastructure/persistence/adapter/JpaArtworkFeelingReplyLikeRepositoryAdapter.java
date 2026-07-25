package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReplyLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyLikeRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkFeelingReplyLikeJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkFeelingReplyLikeRepositoryAdapter
    implements ArtworkFeelingReplyLikeRepository {

  private final ArtworkFeelingReplyLikeJpaRepository repository;

  @Override
  public Optional<ArtworkFeelingReplyLikeSnapshot> toggleAndGetSnapshot(
      Long feelingReplyId, Long userId) {
    repository.lockByFeelingReplyId(feelingReplyId);
    repository.toggle(feelingReplyId, userId);

    long likeCount = repository.countByFeelingReplyIdAndDeletedAtIsNull(feelingReplyId);
    return repository
        .findByFeelingReplyIdAndUserId(feelingReplyId, userId)
        .map(replyLike -> toSnapshot(replyLike, likeCount));
  }

  private ArtworkFeelingReplyLikeSnapshot toSnapshot(
      ArtworkFeelingReplyLike replyLike, long likeCount) {
    return new ArtworkFeelingReplyLikeSnapshot(
        replyLike.getFeelingReplyId(),
        !replyLike.isDeleted(),
        likeCount,
        replyLike.getCreatedAt(),
        replyLike.getDeletedAt());
  }
}
