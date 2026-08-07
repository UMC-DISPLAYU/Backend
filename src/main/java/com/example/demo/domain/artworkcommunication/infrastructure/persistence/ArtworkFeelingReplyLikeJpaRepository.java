package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReplyLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtworkFeelingReplyLikeJpaRepository
    extends JpaRepository<ArtworkFeelingReplyLike, Long> {

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

  @Query(
      """
      SELECT replyLike.feelingReplyId
      FROM ArtworkFeelingReplyLike replyLike
      WHERE replyLike.feelingReplyId IN :feelingReplyIds
        AND replyLike.userId = :userId
        AND replyLike.deletedAt IS NULL
      """)
  List<Long> findLikedFeelingReplyIds(
      @Param("feelingReplyIds") List<Long> feelingReplyIds, @Param("userId") Long userId);
}
