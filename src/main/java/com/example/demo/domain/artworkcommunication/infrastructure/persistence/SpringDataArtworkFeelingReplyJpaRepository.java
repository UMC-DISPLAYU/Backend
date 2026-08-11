package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataArtworkFeelingReplyJpaRepository
    extends JpaRepository<ArtworkFeelingReply, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
      SELECT reply
      FROM ArtworkFeelingReply reply
      WHERE reply.feelingReplyId = :feelingReplyId
        AND reply.deletedAt IS NULL
      """)
  Optional<ArtworkFeelingReply> findActiveByIdForUpdate(
      @Param("feelingReplyId") Long feelingReplyId);

  @Query(
      """
      SELECT reply
      FROM ArtworkFeelingReply reply
      WHERE reply.feelingId = :feelingId
        AND reply.deletedAt IS NULL
        AND (:cursorId IS NULL OR reply.feelingReplyId > :cursorId)
      ORDER BY reply.feelingReplyId ASC
      """)
  List<ArtworkFeelingReply> findActiveByFeelingIdWithCursor(
      @Param("feelingId") Long feelingId, @Param("cursorId") Long cursorId, Pageable pageable);

  @Query(
      """
      SELECT reply.feelingId, COUNT(reply)
      FROM ArtworkFeelingReply reply
      WHERE reply.feelingId IN :feelingIds
        AND reply.deletedAt IS NULL
      GROUP BY reply.feelingId
      """)
  List<Object[]> countActiveByFeelingIds(@Param("feelingIds") List<Long> feelingIds);
}
