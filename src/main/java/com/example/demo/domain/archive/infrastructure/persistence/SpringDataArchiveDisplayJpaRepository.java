package com.example.demo.domain.archive.infrastructure.persistence;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataArchiveDisplayJpaRepository extends JpaRepository<ArchiveDisplay, Long> {

  Optional<ArchiveDisplay> findByUserIdAndDisplayId(Long userId, Long displayId);

  @Query(
      """
      SELECT ad
      FROM ArchiveDisplay ad
      WHERE ad.userId = :userId
        AND (
          :cursorId IS NULL
          OR ad.savedAt < (SELECT c.savedAt FROM ArchiveDisplay c WHERE c.id = :cursorId)
          OR (
            ad.savedAt = (SELECT c.savedAt FROM ArchiveDisplay c WHERE c.id = :cursorId)
            AND ad.id < :cursorId
          )
        )
      ORDER BY ad.savedAt DESC, ad.id DESC
      """)
  List<ArchiveDisplay> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      @Param("userId") Long userId, @Param("cursorId") Long cursorId, Pageable pageable);
}
