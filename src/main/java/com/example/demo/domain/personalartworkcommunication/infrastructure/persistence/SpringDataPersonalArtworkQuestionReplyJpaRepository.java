package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataPersonalArtworkQuestionReplyJpaRepository
    extends JpaRepository<PersonalArtworkQuestionReply, Long> {

  List<PersonalArtworkQuestionReply>
      findByPersonalQuestionIdInAndDeletedAtIsNullOrderByCreatedAtAsc(
          List<Long> personalQuestionIds);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
      SELECT reply
      FROM PersonalArtworkQuestionReply reply
      WHERE reply.personalQuestionReplyId = :personalQuestionReplyId
        AND reply.deletedAt IS NULL
      """)
  Optional<PersonalArtworkQuestionReply> findActiveByIdForUpdate(
      @Param("personalQuestionReplyId") Long personalQuestionReplyId);
}
