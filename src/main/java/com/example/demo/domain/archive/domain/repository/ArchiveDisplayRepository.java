package com.example.demo.domain.archive.domain.repository;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import java.util.List;
import java.util.Optional;

public interface ArchiveDisplayRepository {

  Optional<ArchiveDisplay> findById(Long archiveDisplayId);

  Optional<ArchiveDisplay> findByIdAndUserId(Long archiveDisplayId, Long userId);

  Optional<ArchiveDisplay> findByUserIdAndDisplayId(Long userId, Long displayId);

  List<Long> findDisplayIdsByUserIdAndDisplayIdIn(Long userId, List<Long> displayIds);

  List<ArchiveDisplay> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      Long userId, Long cursorId, int limit);

  ArchiveDisplay save(ArchiveDisplay archiveDisplay);

  void delete(ArchiveDisplay archiveDisplay);
}
