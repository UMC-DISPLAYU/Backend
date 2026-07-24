package com.example.demo.domain.displaycommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewRepository;
import java.util.Optional;

import com.example.demo.domain.displaycommunication.infrastructure.persistence.SpringDataDisplayReviewJpaRepository;
import lombok.RequiredArgsConstructor;
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
  public boolean existsByDisplayIdAndUserId(Long displayId, Long userId) {
    return repository.existsByDisplayIdAndUserId(displayId, userId);
  }
}
