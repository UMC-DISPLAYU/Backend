package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalArtworkFeelingReplyJpaRepository
    extends JpaRepository<PersonalArtworkFeelingReply, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
      SELECT reply
      FROM PersonalArtworkFeelingReply reply
      WHERE reply.personalFeelingReplyId = :personalFeelingReplyId
        AND reply.deletedAt IS NULL
      """)
  Optional<PersonalArtworkFeelingReply> findActiveByIdForUpdate(
      @Param("personalFeelingReplyId") Long personalFeelingReplyId);

  @Query(
      """
      SELECT reply
      FROM PersonalArtworkFeelingReply reply
      WHERE reply.personalFeelingId = :personalFeelingId
        AND reply.deletedAt IS NULL
        AND (:cursorId IS NULL OR reply.personalFeelingReplyId > :cursorId)
      ORDER BY reply.personalFeelingReplyId ASC
      """)
  List<PersonalArtworkFeelingReply> findActiveByPersonalFeelingIdWithCursor(
      @Param("personalFeelingId") Long personalFeelingId,
      @Param("cursorId") Long cursorId,
      Pageable pageable);

  @Query(
      """
      SELECT reply.personalFeelingId, COUNT(reply)
      FROM PersonalArtworkFeelingReply reply
      WHERE reply.personalFeelingId IN :personalFeelingIds
        AND reply.deletedAt IS NULL
      GROUP BY reply.personalFeelingId
      """)
  List<Object[]> countActiveByPersonalFeelingIds(
      @Param("personalFeelingIds") List<Long> personalFeelingIds);
}
