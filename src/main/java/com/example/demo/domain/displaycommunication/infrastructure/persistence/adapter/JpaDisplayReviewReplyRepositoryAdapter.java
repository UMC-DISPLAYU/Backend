package com.example.demo.domain.displaycommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewReplyRepository;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.DisplayReviewReplyJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaDisplayReviewReplyRepositoryAdapter implements DisplayReviewReplyRepository {
  private final DisplayReviewReplyJpaRepository repository;

  @Override
  public DisplayReviewReply save(DisplayReviewReply displayReviewReply) {
    return repository.save(displayReviewReply);
  }

  @Override
  public Optional<DisplayReviewReply> findById(Long displayReviewReplyId) {
    return repository.findById(displayReviewReplyId);
  }

  @Override
  public void softDeleteAllByDisplayReviewId(Long displayReviewId) {
    repository.softDeleteAllByDisplayReviewId(displayReviewId);
  }

  @Override
  public List<DisplayReviewReply> findActiveByDisplayReviewIdWithCursor(
      Long displayReviewId, Long cursorId, int limit) {
    return repository.findActiveByDisplayReviewIdWithCursor(
        displayReviewId, cursorId, PageRequest.of(0, limit));
  }

  @Override
  public Map<Long, Long> countActiveByDisplayReviewIds(List<Long> displayReviewIds) {
    return repository.countActiveByDisplayReviewIds(displayReviewIds).stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
  }
}
