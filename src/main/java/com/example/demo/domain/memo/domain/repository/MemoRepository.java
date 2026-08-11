package com.example.demo.domain.memo.domain.repository;

import com.example.demo.domain.memo.domain.aggregate.Memo;
import java.util.List;
import java.util.Optional;

public interface MemoRepository {

  Optional<Memo> findByArchiveDisplayIdAndDeletedAtIsNull(Long archiveDisplayId);

  Optional<Memo> findByArchiveWorkIdAndDeletedAtIsNull(Long archiveWorkId);

  List<Memo> findByArchiveDisplayIdInAndDeletedAtIsNull(List<Long> archiveDisplayIds);

  List<Memo> findByArchiveWorkIdInAndDeletedAtIsNull(List<Long> archiveWorkIds);

  // 소프트 삭제 여부와 무관하게 조회한다. Memo는 삭제돼도 row가 남아 FK로 부모(ArchiveDisplay/ArchiveWork)를
  // 계속 참조하므로, 부모를 물리 삭제하기 전에 삭제된 메모까지 포함해 찾아 함께 정리해야 한다.
  Optional<Memo> findByArchiveDisplayId(Long archiveDisplayId);

  Optional<Memo> findByArchiveWorkId(Long archiveWorkId);

  Memo save(Memo memo);

  void delete(Memo memo);
}
