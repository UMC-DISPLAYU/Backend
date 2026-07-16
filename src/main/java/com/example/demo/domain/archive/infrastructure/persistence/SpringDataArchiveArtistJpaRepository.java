package com.example.demo.domain.archive.infrastructure.persistence;

import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataArchiveArtistJpaRepository extends JpaRepository<ArchiveArtist, Long> {

  Optional<ArchiveArtist> findByIdAndUserId(Long archiveArtistId, Long userId);

  Optional<ArchiveArtist> findByUserIdAndCreatorId(Long userId, Long creatorId);

  @Query(
      """
      SELECT a
      FROM ArchiveArtist a
      WHERE a.userId = :userId
        AND (
          :cursorId IS NULL
          OR a.savedAt < (SELECT c.savedAt FROM ArchiveArtist c WHERE c.id = :cursorId AND c.userId = :userId)
          OR (
            a.savedAt = (SELECT c.savedAt FROM ArchiveArtist c WHERE c.id = :cursorId AND c.userId = :userId)
            AND a.id < :cursorId
          )
        )
      ORDER BY a.savedAt DESC, a.id DESC
      """)
  List<ArchiveArtist> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      @Param("userId") Long userId, @Param("cursorId") Long cursorId, Pageable pageable);
}
