package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReplyLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalArtworkFeelingReplyLikeJpaRepository
    extends JpaRepository<PersonalArtworkFeelingReplyLike, Long> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          """
          INSERT INTO PersonalArtworkFeelingReplyLike
            (createdAt, updatedAt, deletedAt, personalFeelingReplyId, userId)
          VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, :personalFeelingReplyId, :userId)
          ON DUPLICATE KEY UPDATE
            updatedAt = CURRENT_TIMESTAMP,
            deletedAt = IF(deletedAt IS NULL, CURRENT_TIMESTAMP, NULL)
          """,
      nativeQuery = true)
  void toggle(
      @Param("personalFeelingReplyId") Long personalFeelingReplyId, @Param("userId") Long userId);

  Optional<PersonalArtworkFeelingReplyLike> findByPersonalFeelingReplyIdAndUserId(
      Long personalFeelingReplyId, Long userId);

  long countByPersonalFeelingReplyIdAndDeletedAtIsNull(Long personalFeelingReplyId);

  @Query(
      """
      SELECT replyLike.personalFeelingReplyId, COUNT(replyLike)
      FROM PersonalArtworkFeelingReplyLike replyLike
      WHERE replyLike.personalFeelingReplyId IN :personalFeelingReplyIds
        AND replyLike.deletedAt IS NULL
      GROUP BY replyLike.personalFeelingReplyId
      """)
  List<Object[]> countByPersonalFeelingReplyIds(
      @Param("personalFeelingReplyIds") List<Long> personalFeelingReplyIds);
}
