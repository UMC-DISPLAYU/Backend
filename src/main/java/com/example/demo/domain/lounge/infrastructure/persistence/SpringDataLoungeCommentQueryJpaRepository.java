package com.example.demo.domain.lounge.infrastructure.persistence;

import com.example.demo.domain.lounge.application.query.LoungeCommentQueryResult;
import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.entity.LoungeCommentImage;
import com.example.demo.domain.lounge.domain.type.LoungeCommentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataLoungeCommentQueryJpaRepository
    extends JpaRepository<LoungeComment, Long> {

  @Query(
      """
      SELECT new com.example.demo.domain.lounge.application.query.LoungeCommentQueryResult(
        comment.id,
        comment.loungePostId,
        comment.parentCommentId,
        comment.authorUserId.value,
        comment.content,
        comment.status,
        comment.createdAt,
        comment.updatedAt
      )
      FROM LoungeComment comment
      WHERE comment.id = :loungeCommentId
        AND comment.status = :status
        AND comment.deletedAt IS NULL
      """)
  Optional<LoungeCommentQueryResult> findActiveById(
      @Param("loungeCommentId") Long loungeCommentId, @Param("status") LoungeCommentStatus status);

  @Query(
      """
      SELECT new com.example.demo.domain.lounge.application.query.LoungeCommentQueryResult(
        comment.id,
        comment.loungePostId,
        comment.parentCommentId,
        comment.authorUserId.value,
        comment.content,
        comment.status,
        comment.createdAt,
        comment.updatedAt
      )
      FROM LoungeComment comment
      WHERE comment.loungePostId = :loungePostId
        AND comment.parentCommentId IS NULL
        AND (
          (comment.status = :activeStatus AND comment.deletedAt IS NULL)
          OR (
            comment.status = :deletedStatus
            AND comment.deletedAt IS NOT NULL
            AND EXISTS (
              SELECT reply.id
              FROM LoungeComment reply
              WHERE reply.parentCommentId = comment.id
                AND reply.status = :activeStatus
                AND reply.deletedAt IS NULL
            )
          )
        )
        AND (:cursorId IS NULL OR comment.id > :cursorId)
      ORDER BY comment.id ASC
      """)
  List<LoungeCommentQueryResult> findVisibleRootByCursor(
      @Param("loungePostId") Long loungePostId,
      @Param("activeStatus") LoungeCommentStatus activeStatus,
      @Param("deletedStatus") LoungeCommentStatus deletedStatus,
      @Param("cursorId") Long cursorId,
      Pageable pageable);

  @Query(
      """
      SELECT new com.example.demo.domain.lounge.application.query.LoungeCommentQueryResult(
        comment.id,
        comment.loungePostId,
        comment.parentCommentId,
        comment.authorUserId.value,
        comment.content,
        comment.status,
        comment.createdAt,
        comment.updatedAt
      )
      FROM LoungeComment comment
      WHERE comment.parentCommentId = :parentCommentId
        AND comment.status = :status
        AND comment.deletedAt IS NULL
        AND (:cursorId IS NULL OR comment.id > :cursorId)
      ORDER BY comment.id ASC
      """)
  List<LoungeCommentQueryResult> findActiveRepliesByCursor(
      @Param("parentCommentId") Long parentCommentId,
      @Param("status") LoungeCommentStatus status,
      @Param("cursorId") Long cursorId,
      Pageable pageable);

  @Query(
      """
      SELECT image
      FROM LoungeComment comment
      JOIN comment.images image
      WHERE comment.id IN :loungeCommentIds
      ORDER BY comment.id ASC, image.sortOrder ASC
      """)
  List<LoungeCommentImage> findImagesByLoungeCommentIds(
      @Param("loungeCommentIds") List<Long> loungeCommentIds);
}
