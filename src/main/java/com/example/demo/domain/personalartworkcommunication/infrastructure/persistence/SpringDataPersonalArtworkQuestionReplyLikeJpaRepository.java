package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReplyLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataPersonalArtworkQuestionReplyLikeJpaRepository
    extends JpaRepository<PersonalArtworkQuestionReplyLike, Long> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          """
          INSERT INTO PersonalArtworkQuestionReplyLike
            (createdAt, updatedAt, deletedAt, personalQuestionReplyId, userId)
          VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, :personalQuestionReplyId, :userId)
          ON DUPLICATE KEY UPDATE
            updatedAt = CURRENT_TIMESTAMP,
            deletedAt = IF(deletedAt IS NULL, CURRENT_TIMESTAMP, NULL)
          """,
      nativeQuery = true)
  void toggle(
      @Param("personalQuestionReplyId") Long personalQuestionReplyId, @Param("userId") Long userId);

  Optional<PersonalArtworkQuestionReplyLike> findByPersonalQuestionReplyIdAndUserId(
      Long personalQuestionReplyId, Long userId);

  long countByPersonalQuestionReplyIdAndDeletedAtIsNull(Long personalQuestionReplyId);

  @Query(
      """
      SELECT replyLike.personalQuestionReplyId, COUNT(replyLike)
      FROM PersonalArtworkQuestionReplyLike replyLike
      WHERE replyLike.personalQuestionReplyId IN :personalQuestionReplyIds
        AND replyLike.deletedAt IS NULL
      GROUP BY replyLike.personalQuestionReplyId
      """)
  List<Object[]> countByPersonalQuestionReplyIds(
      @Param("personalQuestionReplyIds") List<Long> personalQuestionReplyIds);

  @Query(
      """
      SELECT replyLike.personalQuestionReplyId
      FROM PersonalArtworkQuestionReplyLike replyLike
      WHERE replyLike.personalQuestionReplyId IN :personalQuestionReplyIds
        AND replyLike.userId = :userId
        AND replyLike.deletedAt IS NULL
      """)
  List<Long> findLikedPersonalQuestionReplyIds(
      @Param("personalQuestionReplyIds") List<Long> personalQuestionReplyIds,
      @Param("userId") Long userId);
}
