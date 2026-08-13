package com.example.demo.domain.archive.domain.repository;

import com.example.demo.domain.archive.domain.aggregate.ArchivePersonalWork;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArchivePersonalWorkRepository {

  Optional<ArchivePersonalWork> findById(Long archivePersonalWorkId);

  Optional<ArchivePersonalWork> findByIdAndUserId(Long archivePersonalWorkId, Long userId);

  Optional<ArchivePersonalWork> findByUserIdAndPersonalArtworkId(
      Long userId, Long personalArtworkId);

  /**
   * savedAt/signedId(=-id) 기준으로 커서 이전 레코드를 조회한다. 전시 작품(ArchiveWork, signedId=+id)과 하나의 목록으로 병합하기 위해
   * signedId를 음수로 사용해, 동일 userId 내에서 전시 작품과 개인 작품의 저장 기록 ID가 겹쳐도 정렬 우선순위가 항상 구분된다.
   */
  List<ArchivePersonalWork> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      Long userId, LocalDateTime cursorSavedAt, Long cursorSignedId, int limit);

  ArchivePersonalWork save(ArchivePersonalWork archivePersonalWork);

  void delete(ArchivePersonalWork archivePersonalWork);
}
