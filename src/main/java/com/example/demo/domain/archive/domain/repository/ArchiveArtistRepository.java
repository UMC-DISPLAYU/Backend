package com.example.demo.domain.archive.domain.repository;

import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import java.util.List;
import java.util.Optional;

public interface ArchiveArtistRepository {

  Optional<ArchiveArtist> findByIdAndUserId(Long archiveArtistId, Long userId);

  Optional<ArchiveArtist> findByUserIdAndArtistProfileId(Long userId, Long artistProfileId);

  Optional<ArchiveArtist> findByUserIdAndArtistUserId(Long userId, Long artistUserId);

  List<ArchiveArtist> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      Long userId, Long cursorId, int limit);

  ArchiveArtist save(ArchiveArtist archiveArtist);

  void delete(ArchiveArtist archiveArtist);
}
