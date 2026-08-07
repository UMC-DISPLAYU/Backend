package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisplayReviewJpaRepository extends JpaRepository<DisplayReview, Long> {
  @Query(
      """
      SELECT review
      FROM DisplayReview review
      WHERE review.displayId = :displayId
        AND (:cursorId IS NULL OR review.displayReviewId < :cursorId)
      ORDER BY review.displayReviewId DESC
      """)
  List<DisplayReview> findByDisplayIdWithCursor(
      @Param("displayId") Long displayId, @Param("cursorId") Long cursorId, Pageable pageable);
}
