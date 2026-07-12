package com.example.demo.domain.archive.domain.repository;

import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import java.util.List;
import java.util.Optional;

public interface ArchiveWorkRepository {

  Optional<ArchiveWork> findById(Long archiveWorkId);

  Optional<ArchiveWork> findByUserIdAndDisplayArtworkId(Long userId, Long displayArtworkId);

  List<ArchiveWork> findAllByUserIdOrderBySavedAtDescIdDesc(Long userId);

  ArchiveWork save(ArchiveWork archiveWork);

  void delete(ArchiveWork archiveWork);
}
