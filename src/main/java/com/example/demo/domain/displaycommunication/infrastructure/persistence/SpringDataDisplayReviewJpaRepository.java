package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataDisplayReviewJpaRepository extends JpaRepository<DisplayReview, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
      SELECT review
      FROM DisplayReview review
      WHERE review.displayReviewId = :displayReviewId
        AND review.deletedAt IS NULL
      """)
  Optional<DisplayReview> findActiveByIdForUpdate(@Param("displayReviewId") Long displayReviewId);

  @Query(
      """
      SELECT review
      FROM DisplayReview review
      WHERE review.displayId = :displayId
        AND (
          review.deletedAt IS NULL
          OR EXISTS (
            SELECT reply.displayReviewReplyId
            FROM DisplayReviewReply reply
            WHERE reply.displayReviewId = review.displayReviewId
              AND reply.deletedAt IS NULL
          )
        )
        AND (:cursorId IS NULL OR review.displayReviewId > :cursorId)
      ORDER BY review.displayReviewId ASC
      """)
  List<DisplayReview> findByDisplayIdWithCursor(
      @Param("displayId") Long displayId, @Param("cursorId") Long cursorId, Pageable pageable);
}
