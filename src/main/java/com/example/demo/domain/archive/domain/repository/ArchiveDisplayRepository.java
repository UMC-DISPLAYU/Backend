package com.example.demo.domain.archive.domain.repository;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArchiveDisplayRepository {

  Optional<ArchiveDisplay> findById(Long archiveDisplayId);

  Optional<ArchiveDisplay> findByIdAndUserId(Long archiveDisplayId, Long userId);

  Optional<ArchiveDisplay> findByUserIdAndDisplayId(Long userId, Long displayId);

  List<Long> findDisplayIdsByUserIdAndDisplayIdIn(Long userId, List<Long> displayIds);

  List<ArchiveDisplay> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      Long userId, Long cursorId, int limit);

  /** 그 전시를 savedBefore 전에 저장해 두고 취소하지 않은 사용자 ID를 찾는다. */
  List<Long> findSaverUserIdsByDisplayIdSavedBefore(Long displayId, LocalDateTime savedBefore);

  ArchiveDisplay save(ArchiveDisplay archiveDisplay);

  void delete(ArchiveDisplay archiveDisplay);
}
