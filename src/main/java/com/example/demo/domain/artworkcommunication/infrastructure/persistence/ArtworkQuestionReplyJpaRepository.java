package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtworkQuestionReplyJpaRepository
    extends JpaRepository<ArtworkQuestionReply, Long> {

  @Query(
      """
      SELECT reply
      FROM ArtworkQuestionReply reply
      WHERE reply.questionId IN :questionIds
        AND reply.deletedAt IS NULL
      ORDER BY reply.queReplyId ASC
      """)
  List<ArtworkQuestionReply> findActiveByQuestionIds(@Param("questionIds") List<Long> questionIds);
}
