package com.example.demo.domain.displaycommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewLike;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewLikeRepository;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.DisplayReviewLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaDisplayReviewLikeRepositoryAdapter implements DisplayReviewLikeRepository {
  private final DisplayReviewLikeJpaRepository repository;

  @Override
  public DisplayReviewLike save(DisplayReviewLike displayReviewLike) {
    return repository.save(displayReviewLike);
  }

  @Override
  public java.util.Optional<DisplayReviewLike> findByDisplayReviewIdAndUserId(
      Long displayReviewId, Long userId) {
    return repository.findByDisplayReviewIdAndUserId(displayReviewId, userId);
  }

  @Override
  public long countByDisplayReviewIdAndDeletedAtIsNull(Long displayReviewId) {
    return repository.countByDisplayReviewIdAndDeletedAtIsNull(displayReviewId);
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
