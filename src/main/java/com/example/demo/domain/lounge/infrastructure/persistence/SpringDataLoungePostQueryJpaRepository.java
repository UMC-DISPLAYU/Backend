package com.example.demo.domain.lounge.infrastructure.persistence;

import com.example.demo.domain.lounge.application.query.LoungePostQueryResult;
import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungePostImage;
import com.example.demo.domain.lounge.domain.type.LoungeCommentStatus;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
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
        AND post.category IN :categories
        AND post.status = :status
        AND post.deletedAt IS NULL
        AND (:cursorId IS NULL OR post.id < :cursorId)
      ORDER BY post.id DESC
      """)
  List<LoungePostQueryResult> findActiveByAuthorCursor(
      @Param("userId") Long userId,
      @Param("categories") List<LoungePostCategory> categories,
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
      FROM LoungePostScrap postScrap
      JOIN LoungePost post ON post.id = postScrap.loungePostId
      WHERE postScrap.userId.value = :userId
        AND post.category IN :categories
        AND post.status = :status
        AND post.deletedAt IS NULL
        AND (:cursorId IS NULL OR postScrap.id < :cursorId)
      ORDER BY postScrap.id DESC
      """)
  List<LoungePostQueryResult> findActiveScrappedByUserCursor(
      @Param("userId") Long userId,
      @Param("categories") List<LoungePostCategory> categories,
      @Param("status") LoungePostStatus status,
      @Param("cursorId") Long cursorId,
      Pageable pageable);

  @Query(
      """
      SELECT new com.example.demo.domain.lounge.application.query.LoungePostQueryResult(
        MAX(comment.id),
        post.id,
        post.authorUserId.value,
        post.title,
        post.content,
        post.category,
        post.createdAt
      )
      FROM LoungeComment comment
      JOIN LoungePost post ON post.id = comment.loungePostId
      WHERE comment.authorUserId.value = :userId
        AND post.category IN :categories
        AND comment.status = :commentStatus
        AND comment.deletedAt IS NULL
        AND post.status = :postStatus
        AND post.deletedAt IS NULL
      GROUP BY post.id, post.authorUserId.value, post.title, post.content, post.category, post.createdAt
      HAVING (:cursorId IS NULL OR MAX(comment.id) < :cursorId)
      ORDER BY MAX(comment.id) DESC
      """)
  List<LoungePostQueryResult> findActiveCommentedByUserCursor(
      @Param("userId") Long userId,
      @Param("categories") List<LoungePostCategory> categories,
      @Param("commentStatus") LoungeCommentStatus commentStatus,
      @Param("postStatus") LoungePostStatus postStatus,
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
