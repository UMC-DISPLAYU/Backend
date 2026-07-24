package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayReviewJpaRepository extends JpaRepository<DisplayReview, Long> {
  boolean existsByDisplayIdAndUserId(Long displayId, Long userId);
}
