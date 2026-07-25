package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReplyLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtworkFeelingReplyLikeJpaRepository
    extends JpaRepository<ArtworkFeelingReplyLike, Long> {

  @Query(
      value =
          """
          SELECT *
          FROM ArtworkFeelingReplyLike
          WHERE feelingReplyId = :feelingReplyId
          FOR UPDATE
          """,
      nativeQuery = true)
  List<ArtworkFeelingReplyLike> lockByFeelingReplyId(@Param("feelingReplyId") Long feelingReplyId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          """
          INSERT INTO ArtworkFeelingReplyLike
            (createdAt, updatedAt, deletedAt, feelingReplyId, userId)
          VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, :feelingReplyId, :userId)
          ON DUPLICATE KEY UPDATE
            updatedAt = CURRENT_TIMESTAMP,
            deletedAt = IF(deletedAt IS NULL, CURRENT_TIMESTAMP, NULL)
          """,
      nativeQuery = true)
  void toggle(@Param("feelingReplyId") Long feelingReplyId, @Param("userId") Long userId);

  Optional<ArtworkFeelingReplyLike> findByFeelingReplyIdAndUserId(Long feelingReplyId, Long userId);

  long countByFeelingReplyIdAndDeletedAtIsNull(Long feelingReplyId);

  @Query(
      """
      SELECT replyLike.feelingReplyId, COUNT(replyLike)
      FROM ArtworkFeelingReplyLike replyLike
      WHERE replyLike.feelingReplyId IN :feelingReplyIds
        AND replyLike.deletedAt IS NULL
      GROUP BY replyLike.feelingReplyId
      """)
  List<Object[]> countByFeelingReplyIds(@Param("feelingReplyIds") List<Long> feelingReplyIds);
}
