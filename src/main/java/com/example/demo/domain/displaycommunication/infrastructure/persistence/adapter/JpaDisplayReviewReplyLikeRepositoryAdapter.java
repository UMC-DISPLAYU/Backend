package com.example.demo.domain.displaycommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReplyLike;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyLikeRepository;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.DisplayReviewReplyLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaDisplayReviewReplyLikeRepositoryAdapter
    implements DisplayReviewReplyLikeRepository {

  private final DisplayReviewReplyLikeJpaRepository repository;

  @Override
  public Optional<DisplayReviewReplyLikeSnapshot> likeAndGetSnapshot(
      Long displayReviewReplyId, Long userId) {
    repository.lockByDisplayReviewReplyId(displayReviewReplyId);
    repository.insertIfAbsent(displayReviewReplyId, userId);

    long likeCount = repository.countByDisplayReviewReplyId(displayReviewReplyId);
    return repository
        .findByDisplayReviewReplyIdAndUserId(displayReviewReplyId, userId)
        .map(replyLike -> toSnapshot(replyLike, likeCount));
  }

  @Override
  public Optional<DisplayReviewReplyLikeSnapshot> deleteAndGetSnapshot(
      Long displayReviewReplyId, Long userId) {
    repository.lockByDisplayReviewReplyId(displayReviewReplyId);
    int deleted = repository.deleteByDisplayReviewReplyIdAndUserId(displayReviewReplyId, userId);
    if (deleted == 0) {
      return Optional.empty();
    }
    long likeCount = repository.countByDisplayReviewReplyId(displayReviewReplyId);
    return Optional.of(
        new DisplayReviewReplyLikeSnapshot(displayReviewReplyId, false, likeCount, null, null));
  }

  private DisplayReviewReplyLikeSnapshot toSnapshot(
      DisplayReviewReplyLike replyLike, long likeCount) {
    return new DisplayReviewReplyLikeSnapshot(
        replyLike.getDisplayReviewReplyId(), true, likeCount, replyLike.getCreatedAt(), null);
  }

  @Override
  public Map<Long, Long> countByDisplayReviewReplyIds(List<Long> displayReviewReplyIds) {
    return repository.countByDisplayReviewReplyIds(displayReviewReplyIds).stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
  }

  @Override
  public Set<Long> findLikedDisplayReviewReplyIds(List<Long> displayReviewReplyIds, Long userId) {
    return Set.copyOf(repository.findLikedDisplayReviewReplyIds(displayReviewReplyIds, userId));
  }
}
