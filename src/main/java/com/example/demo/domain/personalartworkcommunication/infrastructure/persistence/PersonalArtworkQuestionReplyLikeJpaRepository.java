package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReplyLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalArtworkQuestionReplyLikeJpaRepository
    extends JpaRepository<PersonalArtworkQuestionReplyLike, Long> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          """
          INSERT INTO PersonalArtworkQuestionReplyLike
            (createdAt, updatedAt, deletedAt, personalQuestionReplyId, userId)
          VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, :personalQuestionReplyId, :userId)
          ON DUPLICATE KEY UPDATE
            personalQuestionReplyLikeId = personalQuestionReplyLikeId
          """,
      nativeQuery = true)
  void insertIfAbsent(
      @Param("personalQuestionReplyId") Long personalQuestionReplyId, @Param("userId") Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
      DELETE FROM PersonalArtworkQuestionReplyLike replyLike
      WHERE replyLike.personalQuestionReplyId = :personalQuestionReplyId
        AND replyLike.userId = :userId
      """)
  int deleteByPersonalQuestionReplyIdAndUserId(
      @Param("personalQuestionReplyId") Long personalQuestionReplyId, @Param("userId") Long userId);

  Optional<PersonalArtworkQuestionReplyLike> findByPersonalQuestionReplyIdAndUserId(
      Long personalQuestionReplyId, Long userId);

  long countByPersonalQuestionReplyId(Long personalQuestionReplyId);

  @Query(
      """
      SELECT replyLike.personalQuestionReplyId, COUNT(replyLike)
      FROM PersonalArtworkQuestionReplyLike replyLike
      WHERE replyLike.personalQuestionReplyId IN :personalQuestionReplyIds
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
      """)
  List<Long> findLikedPersonalQuestionReplyIds(
      @Param("personalQuestionReplyIds") List<Long> personalQuestionReplyIds,
      @Param("userId") Long userId);
}
