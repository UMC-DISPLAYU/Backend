package com.example.demo.domain.displaycommunication.domain.repository;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;

public interface DisplayReviewRepository {
  DisplayReview save(DisplayReview displayReview);

  boolean existsByDisplayIdAndUserId(Long displayId, Long userId);
}
