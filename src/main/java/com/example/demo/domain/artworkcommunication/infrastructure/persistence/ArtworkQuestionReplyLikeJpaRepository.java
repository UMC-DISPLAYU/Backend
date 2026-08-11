package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReplyLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtworkQuestionReplyLikeJpaRepository
    extends JpaRepository<ArtworkQuestionReplyLike, Long> {

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

  @Query(
      """
      SELECT replyLike.questionReplyId
      FROM ArtworkQuestionReplyLike replyLike
      WHERE replyLike.questionReplyId IN :questionReplyIds
        AND replyLike.userId = :userId
        AND replyLike.deletedAt IS NULL
      """)
  List<Long> findLikedQuestionReplyIds(
      @Param("questionReplyIds") List<Long> questionReplyIds, @Param("userId") Long userId);
}
