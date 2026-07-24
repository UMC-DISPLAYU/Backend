package com.example.demo.domain.displaycommunication.domain.repository;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import java.util.Optional;

public interface DisplayReviewRepository {
  DisplayReview save(DisplayReview displayReview);

  Optional<DisplayReview> findById(Long displayReviewId);

  boolean existsByDisplayIdAndUserId(Long displayId, Long userId);
}
