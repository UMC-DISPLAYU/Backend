package com.example.demo.domain.archive.infrastructure.persistence;

import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataArchiveArtistJpaRepository extends JpaRepository<ArchiveArtist, Long> {

  Optional<ArchiveArtist> findByIdAndUserIdAndDeletedAtIsNull(Long archiveArtistId, Long userId);

  Optional<ArchiveArtist> findByUserIdAndArtistProfileIdAndDeletedAtIsNull(
      Long userId, Long artistProfileId);

  Optional<ArchiveArtist> findByUserIdAndArtistUserIdAndDeletedAtIsNull(
      Long userId, Long artistUserId);

  @Query(
      """
      SELECT a
      FROM ArchiveArtist a
      WHERE a.userId = :userId
        AND a.deletedAt IS NULL
        AND (
          :cursorId IS NULL
          OR a.savedAt < (SELECT c.savedAt FROM ArchiveArtist c WHERE c.id = :cursorId AND c.userId = :userId AND c.deletedAt IS NULL)
          OR (
            a.savedAt = (SELECT c.savedAt FROM ArchiveArtist c WHERE c.id = :cursorId AND c.userId = :userId AND c.deletedAt IS NULL)
            AND a.id < :cursorId
          )
        )
      ORDER BY a.savedAt DESC, a.id DESC
      """)
  List<ArchiveArtist> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      @Param("userId") Long userId, @Param("cursorId") Long cursorId, Pageable pageable);
}
