package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReplyLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtworkQuestionReplyLikeJpaRepository
    extends JpaRepository<ArtworkQuestionReplyLike, Long> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          """
          INSERT INTO ArtworkQuestionReplyLike
            (createdAt, updatedAt, deletedAt, questionReplyId, userId)
          VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, :questionReplyId, :userId)
          ON DUPLICATE KEY UPDATE
            updatedAt = CURRENT_TIMESTAMP,
            deletedAt = IF(deletedAt IS NULL, CURRENT_TIMESTAMP, NULL)
          """,
      nativeQuery = true)
  void toggle(@Param("questionReplyId") Long questionReplyId, @Param("userId") Long userId);

  Optional<ArtworkQuestionReplyLike> findByQuestionReplyIdAndUserId(
      Long questionReplyId, Long userId);

  long countByQuestionReplyIdAndDeletedAtIsNull(Long questionReplyId);

  @Query(
      """
      SELECT replyLike.questionReplyId, COUNT(replyLike)
      FROM ArtworkQuestionReplyLike replyLike
      WHERE replyLike.questionReplyId IN :questionReplyIds
        AND replyLike.deletedAt IS NULL
      GROUP BY replyLike.questionReplyId
      """)
  List<Object[]> countByQuestionReplyIds(@Param("questionReplyIds") List<Long> questionReplyIds);
}
