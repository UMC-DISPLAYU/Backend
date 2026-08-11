package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReplyLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataArtworkFeelingReplyLikeJpaRepository
    extends JpaRepository<ArtworkFeelingReplyLike, Long> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          """
          INSERT INTO ArtworkFeelingReplyLike
            (createdAt, updatedAt, deletedAt, feelingReplyId, userId)
          VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, :feelingReplyId, :userId)
          ON DUPLICATE KEY UPDATE
            feelingReplyLikeId = feelingReplyLikeId
          """,
      nativeQuery = true)
  void insertIfAbsent(@Param("feelingReplyId") Long feelingReplyId, @Param("userId") Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
      DELETE FROM ArtworkFeelingReplyLike replyLike
      WHERE replyLike.feelingReplyId = :feelingReplyId
        AND replyLike.userId = :userId
      """)
  int deleteByFeelingReplyIdAndUserId(
      @Param("feelingReplyId") Long feelingReplyId, @Param("userId") Long userId);

  Optional<ArtworkFeelingReplyLike> findByFeelingReplyIdAndUserId(Long feelingReplyId, Long userId);

  long countByFeelingReplyId(Long feelingReplyId);

  @Query(
      """
      SELECT replyLike.feelingReplyId, COUNT(replyLike)
      FROM ArtworkFeelingReplyLike replyLike
      WHERE replyLike.feelingReplyId IN :feelingReplyIds
      GROUP BY replyLike.feelingReplyId
      """)
  List<Object[]> countByFeelingReplyIds(@Param("feelingReplyIds") List<Long> feelingReplyIds);

  @Query(
      """
      SELECT replyLike.feelingReplyId
      FROM ArtworkFeelingReplyLike replyLike
      WHERE replyLike.feelingReplyId IN :feelingReplyIds
        AND replyLike.userId = :userId
      """)
  List<Long> findLikedFeelingReplyIds(
      @Param("feelingReplyIds") List<Long> feelingReplyIds, @Param("userId") Long userId);
}
