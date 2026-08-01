package com.example.demo.domain.lounge.infrastructure.persistence;

import com.example.demo.domain.lounge.application.query.LoungePostQueryResult;
import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungePostImage;
import com.example.demo.domain.lounge.domain.type.LoungePostStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface SpringDataLoungePostQueryJpaRepository extends Repository<LoungePost, Long> {

  @Query(
      """
      SELECT new com.example.demo.domain.lounge.application.query.LoungePostQueryResult(
        post.id,
        post.id,
        post.authorUserId.value,
        post.title,
        post.content,
        post.category,
        post.createdAt
      )
      FROM LoungePost post
      WHERE post.authorUserId.value = :userId
        AND post.status = :status
        AND post.deletedAt IS NULL
        AND (:cursorId IS NULL OR post.id < :cursorId)
      ORDER BY post.id DESC
      """)
  List<LoungePostQueryResult> findActiveByAuthorCursor(
      @Param("userId") Long userId,
      @Param("status") LoungePostStatus status,
      @Param("cursorId") Long cursorId,
      Pageable pageable);

  @Query(
      """
      SELECT new com.example.demo.domain.lounge.application.query.LoungePostQueryResult(
        postScrap.id,
        post.id,
        post.authorUserId.value,
        post.title,
        post.content,
        post.category,
        post.createdAt
      )
      FROM LoungePostScrap postScrap, LoungePost post
      WHERE postScrap.userId.value = :userId
        AND post.id = postScrap.loungePostId
        AND post.status = :status
        AND post.deletedAt IS NULL
        AND (:cursorId IS NULL OR postScrap.id < :cursorId)
      ORDER BY postScrap.id DESC
      """)
  List<LoungePostQueryResult> findActiveScrappedByUserCursor(
      @Param("userId") Long userId,
      @Param("status") LoungePostStatus status,
      @Param("cursorId") Long cursorId,
      Pageable pageable);

  @Query(
      """
      SELECT image
      FROM LoungePostImage image
      WHERE image.loungePost.id IN :loungePostIds
      ORDER BY image.loungePost.id, image.sortOrder
      """)
  List<LoungePostImage> findImagesByLoungePostIds(@Param("loungePostIds") List<Long> loungePostIds);
}
