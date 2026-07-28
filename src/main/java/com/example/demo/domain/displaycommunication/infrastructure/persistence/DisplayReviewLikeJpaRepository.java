package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisplayReviewLikeJpaRepository extends JpaRepository<DisplayReviewLike, Long> {

  @Query(
      value =
          """
          SELECT *
          FROM DisplayReviewLike
          WHERE displayReviewId = :displayReviewId
          FOR UPDATE
          """,
      nativeQuery = true)
  List<DisplayReviewLike> lockByDisplayReviewId(@Param("displayReviewId") Long displayReviewId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          """
          INSERT INTO DisplayReviewLike
            (createdAt, updatedAt, deletedAt, displayReviewId, userId)
          VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, :displayReviewId, :userId)
          ON DUPLICATE KEY UPDATE
            updatedAt = CURRENT_TIMESTAMP,
            deletedAt = IF(deletedAt IS NULL, CURRENT_TIMESTAMP, NULL)
          """,
      nativeQuery = true)
  void toggle(@Param("displayReviewId") Long displayReviewId, @Param("userId") Long userId);

  Optional<DisplayReviewLike> findByDisplayReviewIdAndUserId(Long displayReviewId, Long userId);

  long countByDisplayReviewIdAndDeletedAtIsNull(Long displayReviewId);

  @Query(
      """
      SELECT reviewLike.displayReviewId, COUNT(reviewLike)
      FROM DisplayReviewLike reviewLike
      WHERE reviewLike.displayReviewId IN :displayReviewIds
        AND reviewLike.deletedAt IS NULL
      GROUP BY reviewLike.displayReviewId
      """)
  List<Object[]> countByDisplayReviewIds(@Param("displayReviewIds") List<Long> displayReviewIds);
}
