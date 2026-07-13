package com.example.demo.domain.archive.infrastructure.persistence;

import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataArchiveWorkJpaRepository extends JpaRepository<ArchiveWork, Long> {

  Optional<ArchiveWork> findByIdAndUserId(Long archiveWorkId, Long userId);

  Optional<ArchiveWork> findByUserIdAndDisplayArtworkId(Long userId, Long displayArtworkId);

  @Query(
      """
      SELECT w
      FROM ArchiveWork w
      WHERE w.userId = :userId
        AND (
          :cursorId IS NULL
          OR w.savedAt < (SELECT c.savedAt FROM ArchiveWork c WHERE c.id = :cursorId AND c.userId = :userId)
          OR (
            w.savedAt = (SELECT c.savedAt FROM ArchiveWork c WHERE c.id = :cursorId AND c.userId = :userId)
            AND w.id < :cursorId
          )
        )
      ORDER BY w.savedAt DESC, w.id DESC
      """)
  List<ArchiveWork> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      @Param("userId") Long userId, @Param("cursorId") Long cursorId, Pageable pageable);
}
