package com.example.demo.domain.archive.domain.repository;

import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import java.util.List;
import java.util.Optional;

public interface ArchiveWorkRepository {

  Optional<ArchiveWork> findByIdAndUserId(Long archiveWorkId, Long userId);

  Optional<ArchiveWork> findByUserIdAndDisplayArtworkId(Long userId, Long displayArtworkId);

  List<ArchiveWork> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      Long userId, Long cursorId, int limit);

  ArchiveWork save(ArchiveWork archiveWork);

  void delete(ArchiveWork archiveWork);
}
