package com.example.demo.domain.archive.infrastructure.persistence;

import com.example.demo.domain.archive.domain.aggregate.ArchivePersonalWork;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataArchivePersonalWorkJpaRepository
    extends JpaRepository<ArchivePersonalWork, Long> {

  Optional<ArchivePersonalWork> findByIdAndUserIdAndDeletedAtIsNull(
      Long archivePersonalWorkId, Long userId);

  Optional<ArchivePersonalWork> findByUserIdAndPersonalArtworkIdAndDeletedAtIsNull(
      Long userId, Long personalArtworkId);

  // signedId(=-id)로 정렬해, ArchiveWork(signedId=+id)와 병합했을 때 동일 userId 내 저장 기록 ID가 겹쳐도
  // 정렬 우선순위가 항상 구분되도록 한다.
  @Query(
      """
      SELECT p
      FROM ArchivePersonalWork p
      WHERE p.userId = :userId
        AND p.deletedAt IS NULL
        AND (
          :cursorSavedAt IS NULL
          OR p.savedAt < :cursorSavedAt
          OR (p.savedAt = :cursorSavedAt AND -p.id < :cursorSignedId)
        )
      ORDER BY p.savedAt DESC, -p.id DESC
      """)
  List<ArchivePersonalWork> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      @Param("userId") Long userId,
      @Param("cursorSavedAt") LocalDateTime cursorSavedAt,
      @Param("cursorSignedId") Long cursorSignedId,
      Pageable pageable);
}
