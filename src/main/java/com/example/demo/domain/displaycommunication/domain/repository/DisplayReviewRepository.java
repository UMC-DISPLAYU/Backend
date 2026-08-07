package com.example.demo.domain.displaycommunication.domain.repository;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import java.util.List;
import java.util.Optional;

public interface DisplayReviewRepository {
  DisplayReview save(DisplayReview displayReview);

  Optional<DisplayReview> findById(Long displayReviewId);

  List<DisplayReview> findByDisplayIdWithCursor(Long displayId, Long cursorId, int limit);
}
