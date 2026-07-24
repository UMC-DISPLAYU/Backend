package com.example.demo.domain.displaycommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewLike;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewLikeRepository;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.DisplayReviewLikeJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaDisplayReviewLikeRepositoryAdapter implements DisplayReviewLikeRepository {
  private final DisplayReviewLikeJpaRepository repository;

  @Override
  public Optional<DisplayReviewLikeSnapshot> toggleAndGetSnapshot(
      Long displayReviewId, Long userId) {
    repository.lockByDisplayReviewId(displayReviewId);
    repository.toggle(displayReviewId, userId);

    long likeCount = repository.countByDisplayReviewIdAndDeletedAtIsNull(displayReviewId);
    return repository
        .findByDisplayReviewIdAndUserId(displayReviewId, userId)
        .map(displayReviewLike -> toSnapshot(displayReviewLike, likeCount));
  }

  private DisplayReviewLikeSnapshot toSnapshot(
      DisplayReviewLike displayReviewLike, long likeCount) {
    return new DisplayReviewLikeSnapshot(
        displayReviewLike.getDisplayReviewId(),
        !displayReviewLike.isDeleted(),
        likeCount,
        displayReviewLike.getCreatedAt(),
        displayReviewLike.getDeletedAt());
  }
}
