package com.example.demo.domain.displaycommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewLike;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewLikeRepository;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.DisplayReviewLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaDisplayReviewLikeRepositoryAdapter implements DisplayReviewLikeRepository {
  private final DisplayReviewLikeJpaRepository repository;

  @Override
  public Optional<DisplayReviewLikeSnapshot> likeAndGetSnapshot(Long displayReviewId, Long userId) {
    repository.lockByDisplayReviewId(displayReviewId);
    repository.insertIfAbsent(displayReviewId, userId);

    long likeCount = repository.countByDisplayReviewId(displayReviewId);
    return repository
        .findByDisplayReviewIdAndUserId(displayReviewId, userId)
        .map(displayReviewLike -> toSnapshot(displayReviewLike, likeCount));
  }

  @Override
  public Optional<DisplayReviewLikeSnapshot> deleteAndGetSnapshot(
      Long displayReviewId, Long userId) {
    repository.lockByDisplayReviewId(displayReviewId);
    int deleted = repository.deleteByDisplayReviewIdAndUserId(displayReviewId, userId);
    if (deleted == 0) {
      return Optional.empty();
    }
    long likeCount = repository.countByDisplayReviewId(displayReviewId);
    return Optional.of(
        new DisplayReviewLikeSnapshot(displayReviewId, false, likeCount, null, null));
  }

  private DisplayReviewLikeSnapshot toSnapshot(
      DisplayReviewLike displayReviewLike, long likeCount) {
    return new DisplayReviewLikeSnapshot(
        displayReviewLike.getDisplayReviewId(),
        true,
        likeCount,
        displayReviewLike.getCreatedAt(),
        null);
  }

  @Override
  public Map<Long, Long> countByDisplayReviewIds(List<Long> displayReviewIds) {
    return repository.countByDisplayReviewIds(displayReviewIds).stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
  }

  @Override
  public Set<Long> findLikedDisplayReviewIds(List<Long> displayReviewIds, Long userId) {
    return Set.copyOf(repository.findLikedDisplayReviewIds(displayReviewIds, userId));
  }
}
