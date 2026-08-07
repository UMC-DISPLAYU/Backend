package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReplyLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalArtworkQuestionReplyLikeJpaRepository
    extends JpaRepository<PersonalArtworkQuestionReplyLike, Long> {

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
}
