package com.example.demo.domain.lounge.infrastructure.persistence;

import com.example.demo.domain.lounge.domain.entity.LoungePostLike;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataLoungePostLikeJpaRepository extends JpaRepository<LoungePostLike, Long> {

  @Modifying
  @Query(
      value =
          """
          INSERT INTO LoungePostLike (createdAt, loungePostId, userId)
          VALUES (CURRENT_TIMESTAMP, :loungePostId, :userId)
          ON DUPLICATE KEY UPDATE
            loungePostLikeId = loungePostLikeId
          """,
      nativeQuery = true)
  void insertIfAbsent(@Param("loungePostId") Long loungePostId, @Param("userId") Long userId);

  @Modifying
  @Query(
      """
      DELETE FROM LoungePostLike postLike
      WHERE postLike.loungePostId = :loungePostId AND postLike.userId.value = :userId
      """)
  int deleteByLoungePostIdAndUserId(
      @Param("loungePostId") Long loungePostId, @Param("userId") Long userId);

  long countByLoungePostId(Long loungePostId);

  @Query(
      """
      SELECT postLike.loungePostId, COUNT(postLike)
      FROM LoungePostLike postLike
      WHERE postLike.loungePostId IN :loungePostIds
      GROUP BY postLike.loungePostId
      """)
  List<Object[]> countByLoungePostIds(@Param("loungePostIds") List<Long> loungePostIds);

  @Query(
      """
      SELECT postLike.loungePostId
      FROM LoungePostLike postLike
      WHERE postLike.loungePostId IN :loungePostIds
        AND postLike.userId.value = :userId
      """)
  List<Long> findLikedLoungePostIds(
      @Param("loungePostIds") List<Long> loungePostIds, @Param("userId") Long userId);
}
