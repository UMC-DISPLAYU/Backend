package com.example.demo.domain.lounge.infrastructure.persistence;

import com.example.demo.domain.lounge.domain.entity.LoungeCommentLike;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataLoungeCommentLikeJpaRepository
    extends JpaRepository<LoungeCommentLike, Long> {

  @Modifying
  @Query(
      value =
          """
          INSERT IGNORE INTO LoungeCommentLike (loungeCommentId, userId)
          VALUES (:loungeCommentId, :userId)
          """,
      nativeQuery = true)
  void insertIgnore(@Param("loungeCommentId") Long loungeCommentId, @Param("userId") Long userId);

  @Modifying
  @Query(
      """
      DELETE FROM LoungeCommentLike commentLike
      WHERE commentLike.loungeCommentId = :loungeCommentId AND commentLike.userId.value = :userId
      """)
  void deleteByLoungeCommentIdAndUserId(
      @Param("loungeCommentId") Long loungeCommentId, @Param("userId") Long userId);

  long countByLoungeCommentId(Long loungeCommentId);

  @Query(
      """
      SELECT commentLike.loungeCommentId, COUNT(commentLike)
      FROM LoungeCommentLike commentLike
      WHERE commentLike.loungeCommentId IN :loungeCommentIds
      GROUP BY commentLike.loungeCommentId
      """)
  List<Object[]> countByLoungeCommentIds(@Param("loungeCommentIds") List<Long> loungeCommentIds);

  @Query(
      """
      SELECT commentLike.loungeCommentId
      FROM LoungeCommentLike commentLike
      WHERE commentLike.loungeCommentId IN :loungeCommentIds
        AND commentLike.userId.value = :userId
      """)
  List<Long> findLikedLoungeCommentIds(
      @Param("loungeCommentIds") List<Long> loungeCommentIds, @Param("userId") Long userId);
}
