package com.example.demo.domain.lounge.infrastructure.persistence;

import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.type.LoungeCommentStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataLoungeCommentJpaRepository extends JpaRepository<LoungeComment, Long> {

  List<LoungeComment> findByLoungePostIdAndStatusAndDeletedAtIsNullOrderByCreatedAtAscIdAsc(
      Long loungePostId, LoungeCommentStatus status);

  @Query(
      """
      SELECT comment
      FROM LoungeComment comment
      WHERE comment.loungePostId = :loungePostId
        AND comment.parentCommentId IS NULL
        AND comment.status = :status
        AND comment.deletedAt IS NULL
        AND (:cursorId IS NULL OR comment.id > :cursorId)
      ORDER BY comment.id ASC
      """)
  List<LoungeComment> findActiveRootByCursor(
      @Param("loungePostId") Long loungePostId,
      @Param("status") LoungeCommentStatus status,
      @Param("cursorId") Long cursorId,
      Pageable pageable);

  @Query(
      """
      SELECT comment
      FROM LoungeComment comment
      WHERE comment.parentCommentId = :parentCommentId
        AND comment.status = :status
        AND comment.deletedAt IS NULL
        AND (:cursorId IS NULL OR comment.id > :cursorId)
      ORDER BY comment.id ASC
      """)
  List<LoungeComment> findActiveRepliesByCursor(
      @Param("parentCommentId") Long parentCommentId,
      @Param("status") LoungeCommentStatus status,
      @Param("cursorId") Long cursorId,
      Pageable pageable);

  long countByLoungePostIdAndStatusAndDeletedAtIsNull(
      Long loungePostId, LoungeCommentStatus status);

  @Query(
      """
      SELECT comment.loungePostId, COUNT(comment)
      FROM LoungeComment comment
      WHERE comment.loungePostId IN :loungePostIds
        AND comment.status = :status
        AND comment.deletedAt IS NULL
      GROUP BY comment.loungePostId
      """)
  List<Object[]> countByLoungePostIdsAndStatusAndDeletedAtIsNull(
      @Param("loungePostIds") List<Long> loungePostIds,
      @Param("status") LoungeCommentStatus status);

  @Query(
      """
      SELECT comment.parentCommentId, COUNT(comment)
      FROM LoungeComment comment
      WHERE comment.parentCommentId IN :parentCommentIds
        AND comment.status = :status
        AND comment.deletedAt IS NULL
      GROUP BY comment.parentCommentId
      """)
  List<Object[]> countRepliesByParentCommentIdsAndStatusAndDeletedAtIsNull(
      @Param("parentCommentIds") List<Long> parentCommentIds,
      @Param("status") LoungeCommentStatus status);
}
