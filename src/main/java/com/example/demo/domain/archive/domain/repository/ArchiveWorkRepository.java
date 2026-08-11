package com.example.demo.domain.archive.domain.repository;

import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArchiveWorkRepository {

  Optional<ArchiveWork> findById(Long archiveWorkId);

  Optional<ArchiveWork> findByIdAndUserId(Long archiveWorkId, Long userId);

  Optional<ArchiveWork> findByUserIdAndDisplayArtworkId(Long userId, Long displayArtworkId);

  /**
   * savedAt/signedId(=+id) 기준으로 커서 이전 레코드를 조회한다. 개인 작품(ArchivePersonalWork, signedId=-id)과 하나의 목록으로
   * 병합하기 위해 signedId를 양수로 사용해, 동일 userId 내에서 전시 작품과 개인 작품의 저장 기록 ID가 겹쳐도 정렬 우선순위가 항상 구분된다.
   */
  List<ArchiveWork> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      Long userId, LocalDateTime cursorSavedAt, Long cursorSignedId, int limit);

  ArchiveWork save(ArchiveWork archiveWork);

  void delete(ArchiveWork archiveWork);
}
