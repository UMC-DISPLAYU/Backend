package com.example.demo.domain.lounge.infrastructure.persistence;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.type.LoungePostStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataLoungePostJpaRepository extends JpaRepository<LoungePost, Long> {

  @Query(
      """
      SELECT post
      FROM LoungePost post
      WHERE post.status = :status
        AND post.deletedAt IS NULL
        AND (:cursorId IS NULL OR post.id < :cursorId)
      ORDER BY post.id DESC
      """)
  List<LoungePost> findActiveByCursor(
      @Param("status") LoungePostStatus status,
      @Param("cursorId") Long cursorId,
      Pageable pageable);

  @Query(
      """
      SELECT post
      FROM LoungePost post
      WHERE post.status = :status
        AND post.deletedAt IS NULL
        AND post.category = :category
        AND (:cursorId IS NULL OR post.id < :cursorId)
      ORDER BY post.id DESC
      """)
  List<LoungePost> findActiveByCategoryAndCursor(
      @Param("status") LoungePostStatus status,
      @Param("category") LoungePostCategory category,
      @Param("cursorId") Long cursorId,
      Pageable pageable);
}
