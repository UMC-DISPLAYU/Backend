package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReplyLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisplayReviewReplyLikeJpaRepository
    extends JpaRepository<DisplayReviewReplyLike, Long> {

  @Query(
      value =
          """
          SELECT *
          FROM DisplayReviewReplyLike
          WHERE displayReviewReplyId = :displayReviewReplyId
          FOR UPDATE
          """,
      nativeQuery = true)
  List<DisplayReviewReplyLike> lockByDisplayReviewReplyId(
      @Param("displayReviewReplyId") Long displayReviewReplyId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          """
          INSERT INTO DisplayReviewReplyLike
            (createdAt, updatedAt, deletedAt, displayReviewReplyId, userId)
          VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, :displayReviewReplyId, :userId)
          ON DUPLICATE KEY UPDATE
            updatedAt = CURRENT_TIMESTAMP,
            deletedAt = IF(deletedAt IS NULL, CURRENT_TIMESTAMP, NULL)
          """,
      nativeQuery = true)
  void toggle(
      @Param("displayReviewReplyId") Long displayReviewReplyId, @Param("userId") Long userId);

  Optional<DisplayReviewReplyLike> findByDisplayReviewReplyIdAndUserId(
      Long displayReviewReplyId, Long userId);

  long countByDisplayReviewReplyIdAndDeletedAtIsNull(Long displayReviewReplyId);

  @Query(
      """
      SELECT replyLike.displayReviewReplyId, COUNT(replyLike)
      FROM DisplayReviewReplyLike replyLike
      WHERE replyLike.displayReviewReplyId IN :displayReviewReplyIds
        AND replyLike.deletedAt IS NULL
      GROUP BY replyLike.displayReviewReplyId
      """)
  List<Object[]> countByDisplayReviewReplyIds(
      @Param("displayReviewReplyIds") List<Long> displayReviewReplyIds);
}
