package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataDisplayReviewReplyJpaRepository
    extends JpaRepository<DisplayReviewReply, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
      SELECT reply
      FROM DisplayReviewReply reply
      WHERE reply.displayReviewReplyId = :displayReviewReplyId
        AND reply.deletedAt IS NULL
      """)
  Optional<DisplayReviewReply> findActiveByIdForUpdate(
      @Param("displayReviewReplyId") Long displayReviewReplyId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
      SELECT reply
      FROM DisplayReviewReply reply
      WHERE reply.displayReviewId = :displayReviewId
        AND reply.deletedAt IS NULL
      ORDER BY reply.displayReviewReplyId ASC
      """)
  List<DisplayReviewReply> findActiveByDisplayReviewIdForUpdate(
      @Param("displayReviewId") Long displayReviewId);

  @Query(
      """
      SELECT reply
      FROM DisplayReviewReply reply
      WHERE reply.displayReviewId = :displayReviewId
        AND reply.deletedAt IS NULL
        AND (:cursorId IS NULL OR reply.displayReviewReplyId > :cursorId)
      ORDER BY reply.displayReviewReplyId ASC
      """)
  List<DisplayReviewReply> findActiveByDisplayReviewIdWithCursor(
      @Param("displayReviewId") Long displayReviewId,
      @Param("cursorId") Long cursorId,
      Pageable pageable);

  @Query(
      """
      SELECT reply.displayReviewId, COUNT(reply)
      FROM DisplayReviewReply reply
      WHERE reply.displayReviewId IN :displayReviewIds
        AND reply.deletedAt IS NULL
      GROUP BY reply.displayReviewId
      """)
  List<Object[]> countActiveByDisplayReviewIds(
      @Param("displayReviewIds") List<Long> displayReviewIds);
}
