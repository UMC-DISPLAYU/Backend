package com.example.demo.domain.lounge.infrastructure.persistence;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.type.LoungePostStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataLoungePostJpaRepository extends JpaRepository<LoungePost, Long> {

  @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
  Optional<LoungePost> findWithOptimisticLockById(Long loungePostId);

  @Query(
      """
      SELECT post
      FROM LoungePost post
      WHERE post.status = :status
        AND post.deletedAt IS NULL
        AND post.category IN :categories
        AND (:cursorId IS NULL OR post.id < :cursorId)
      ORDER BY post.id DESC
      """)
  List<LoungePost> findActiveByCategoriesAndCursor(
      @Param("status") LoungePostStatus status,
      @Param("categories") List<LoungePostCategory> categories,
      @Param("cursorId") Long cursorId,
      Pageable pageable);
}
