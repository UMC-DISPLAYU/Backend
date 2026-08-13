package com.example.demo.domain.archive.infrastructure.persistence;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataArchiveDisplayJpaRepository extends JpaRepository<ArchiveDisplay, Long> {

  Optional<ArchiveDisplay> findByIdAndUserIdAndDeletedAtIsNull(Long archiveDisplayId, Long userId);

  Optional<ArchiveDisplay> findByUserIdAndDisplayIdAndDeletedAtIsNull(Long userId, Long displayId);

  @Query(
      """
      SELECT ad.displayId
      FROM ArchiveDisplay ad
      JOIN Display display ON display.id = ad.displayId
      WHERE ad.userId = :userId
        AND ad.displayId IN :displayIds
        AND ad.deletedAt IS NULL
        AND display.status = com.example.demo.domain.display.domain.type.DisplayStatus.PUBLISHED
        AND display.deletedAt IS NULL
      """)
  List<Long> findDisplayIdsByUserIdAndDisplayIdIn(
      @Param("userId") Long userId, @Param("displayIds") List<Long> displayIds);

  @Query(
      """
      SELECT ad
      FROM ArchiveDisplay ad
      JOIN Display display ON display.id = ad.displayId
      WHERE ad.userId = :userId
        AND ad.deletedAt IS NULL
        AND display.status = com.example.demo.domain.display.domain.type.DisplayStatus.PUBLISHED
        AND display.deletedAt IS NULL
        AND (
          :cursorId IS NULL
          OR ad.savedAt < (SELECT c.savedAt FROM ArchiveDisplay c WHERE c.id = :cursorId AND c.userId = :userId)
          OR (
            ad.savedAt = (SELECT c.savedAt FROM ArchiveDisplay c WHERE c.id = :cursorId AND c.userId = :userId)
            AND ad.id < :cursorId
          )
        )
      ORDER BY ad.savedAt DESC, ad.id DESC
      """)
  List<ArchiveDisplay> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      @Param("userId") Long userId, @Param("cursorId") Long cursorId, Pageable pageable);
}
