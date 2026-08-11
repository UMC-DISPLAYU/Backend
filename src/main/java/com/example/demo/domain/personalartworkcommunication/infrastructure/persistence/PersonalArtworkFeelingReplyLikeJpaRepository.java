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
            personalFeelingReplyLikeId = personalFeelingReplyLikeId
          """,
      nativeQuery = true)
  void insertIfAbsent(
      @Param("personalFeelingReplyId") Long personalFeelingReplyId, @Param("userId") Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
      DELETE FROM PersonalArtworkFeelingReplyLike replyLike
      WHERE replyLike.personalFeelingReplyId = :personalFeelingReplyId
        AND replyLike.userId = :userId
      """)
  int deleteByPersonalFeelingReplyIdAndUserId(
      @Param("personalFeelingReplyId") Long personalFeelingReplyId, @Param("userId") Long userId);

  Optional<PersonalArtworkFeelingReplyLike> findByPersonalFeelingReplyIdAndUserId(
      Long personalFeelingReplyId, Long userId);

  long countByPersonalFeelingReplyId(Long personalFeelingReplyId);

  @Query(
      """
      SELECT replyLike.personalFeelingReplyId, COUNT(replyLike)
      FROM PersonalArtworkFeelingReplyLike replyLike
      WHERE replyLike.personalFeelingReplyId IN :personalFeelingReplyIds
      GROUP BY replyLike.personalFeelingReplyId
      """)
  List<Object[]> countByPersonalFeelingReplyIds(
      @Param("personalFeelingReplyIds") List<Long> personalFeelingReplyIds);

  @Query(
      """
      SELECT replyLike.personalFeelingReplyId
      FROM PersonalArtworkFeelingReplyLike replyLike
      WHERE replyLike.personalFeelingReplyId IN :personalFeelingReplyIds
        AND replyLike.userId = :userId
      """)
  List<Long> findLikedPersonalFeelingReplyIds(
      @Param("personalFeelingReplyIds") List<Long> personalFeelingReplyIds,
      @Param("userId") Long userId);
}
