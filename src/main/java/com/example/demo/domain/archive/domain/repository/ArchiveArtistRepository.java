package com.example.demo.domain.archive.domain.repository;

import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ArchiveArtistRepository {

  Optional<ArchiveArtist> findByIdAndUserId(Long archiveArtistId, Long userId);

  Optional<ArchiveArtist> findByUserIdAndArtistProfileId(Long userId, Long artistProfileId);

  Optional<ArchiveArtist> findByUserIdAndArtistUserId(Long userId, Long artistUserId);

  List<ArchiveArtist> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      Long userId, Long cursorId, int limit);

  /** 주어진 작가 계정들을 저장해 둔(저장 취소하지 않은) 기록을 모두 찾는다. */
  List<ArchiveArtist> findActiveByArtistUserIds(Collection<Long> artistUserIds);

  ArchiveArtist save(ArchiveArtist archiveArtist);

  void delete(ArchiveArtist archiveArtist);
}
