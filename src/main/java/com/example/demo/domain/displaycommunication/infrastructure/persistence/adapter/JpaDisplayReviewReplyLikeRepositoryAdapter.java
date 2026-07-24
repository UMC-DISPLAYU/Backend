package com.example.demo.domain.displaycommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReplyLike;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyLikeRepository;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.DisplayReviewReplyLikeJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaDisplayReviewReplyLikeRepositoryAdapter
    implements DisplayReviewReplyLikeRepository {

  private final DisplayReviewReplyLikeJpaRepository repository;

  @Override
  public Optional<DisplayReviewReplyLikeSnapshot> toggleAndGetSnapshot(
      Long displayReviewReplyId, Long userId) {
    repository.lockByDisplayReviewReplyId(displayReviewReplyId);
    repository.toggle(displayReviewReplyId, userId);

    long likeCount = repository.countByDisplayReviewReplyIdAndDeletedAtIsNull(displayReviewReplyId);
    return repository
        .findByDisplayReviewReplyIdAndUserId(displayReviewReplyId, userId)
        .map(replyLike -> toSnapshot(replyLike, likeCount));
  }

  private DisplayReviewReplyLikeSnapshot toSnapshot(
      DisplayReviewReplyLike replyLike, long likeCount) {
    return new DisplayReviewReplyLikeSnapshot(
        replyLike.getDisplayReviewReplyId(),
        !replyLike.isDeleted(),
        likeCount,
        replyLike.getCreatedAt(),
        replyLike.getDeletedAt());
  }
}
