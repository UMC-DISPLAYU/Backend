package com.example.demo.domain.displaycommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewRepository;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.SpringDataDisplayReviewJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaDisplayReviewRepositoryAdapter implements DisplayReviewRepository {
  private final SpringDataDisplayReviewJpaRepository repository;

  @Override
  public DisplayReview save(DisplayReview displayReview) {
    return repository.save(displayReview);
  }

  @Override
  public Optional<DisplayReview> findById(Long displayReviewId) {
    return repository.findById(displayReviewId);
  }

  @Override
  public List<DisplayReview> findByDisplayIdWithCursor(Long displayId, Long cursorId, int limit) {
    return repository.findByDisplayIdWithCursor(displayId, cursorId, PageRequest.of(0, limit));
  }
}
