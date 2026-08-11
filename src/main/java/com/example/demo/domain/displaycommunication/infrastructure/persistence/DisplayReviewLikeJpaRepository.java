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
            displayReviewLikeId = displayReviewLikeId
          """,
      nativeQuery = true)
  void insertIfAbsent(@Param("displayReviewId") Long displayReviewId, @Param("userId") Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
      DELETE FROM DisplayReviewLike reviewLike
      WHERE reviewLike.displayReviewId = :displayReviewId
        AND reviewLike.userId = :userId
      """)
  int deleteByDisplayReviewIdAndUserId(
      @Param("displayReviewId") Long displayReviewId, @Param("userId") Long userId);

  Optional<DisplayReviewLike> findByDisplayReviewIdAndUserId(Long displayReviewId, Long userId);

  long countByDisplayReviewId(Long displayReviewId);

  @Query(
      """
      SELECT reviewLike.displayReviewId, COUNT(reviewLike)
      FROM DisplayReviewLike reviewLike
      WHERE reviewLike.displayReviewId IN :displayReviewIds
      GROUP BY reviewLike.displayReviewId
      """)
  List<Object[]> countByDisplayReviewIds(@Param("displayReviewIds") List<Long> displayReviewIds);

  @Query(
      """
      SELECT reviewLike.displayReviewId
      FROM DisplayReviewLike reviewLike
      WHERE reviewLike.displayReviewId IN :displayReviewIds
        AND reviewLike.userId = :userId
      """)
  List<Long> findLikedDisplayReviewIds(
      @Param("displayReviewIds") List<Long> displayReviewIds, @Param("userId") Long userId);
}
