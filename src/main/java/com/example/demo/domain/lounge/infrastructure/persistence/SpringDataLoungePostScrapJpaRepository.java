package com.example.demo.domain.lounge.infrastructure.persistence;

import com.example.demo.domain.lounge.domain.entity.LoungePostScrap;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataLoungePostScrapJpaRepository
    extends JpaRepository<LoungePostScrap, Long> {

  @Modifying
  @Query(
      value =
          """
          INSERT INTO LoungePostScrap (createdAt, loungePostId, userId)
          VALUES (CURRENT_TIMESTAMP, :loungePostId, :userId)
          ON DUPLICATE KEY UPDATE
            loungePostScrapId = loungePostScrapId
          """,
      nativeQuery = true)
  void insertIfAbsent(@Param("loungePostId") Long loungePostId, @Param("userId") Long userId);

  @Modifying
  @Query(
      """
      DELETE FROM LoungePostScrap postScrap
      WHERE postScrap.loungePostId = :loungePostId AND postScrap.userId.value = :userId
      """)
  void deleteByLoungePostIdAndUserId(
      @Param("loungePostId") Long loungePostId, @Param("userId") Long userId);

  long countByLoungePostId(Long loungePostId);

  @Query(
      """
      SELECT postScrap.loungePostId, COUNT(postScrap)
      FROM LoungePostScrap postScrap
      WHERE postScrap.loungePostId IN :loungePostIds
      GROUP BY postScrap.loungePostId
      """)
  List<Object[]> countByLoungePostIds(@Param("loungePostIds") List<Long> loungePostIds);

  @Query(
      """
      SELECT postScrap.loungePostId
      FROM LoungePostScrap postScrap
      WHERE postScrap.loungePostId IN :loungePostIds
        AND postScrap.userId.value = :userId
      """)
  List<Long> findScrappedLoungePostIds(
      @Param("loungePostIds") List<Long> loungePostIds, @Param("userId") Long userId);
}
