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
  // 계속 참조하며, 활성 메모에만 걸리는 유니크 제약(activeArchiveDisplayId/activeArchiveWorkId) 때문에
  // 같은 부모에 대해 소프트 삭제된 메모가 여러 건 쌓일 수 있으므로 List로 모두 조회해 함께 정리해야 한다.
  List<Memo> findAllByArchiveDisplayId(Long archiveDisplayId);

  List<Memo> findAllByArchiveWorkId(Long archiveWorkId);

  Memo save(Memo memo);

  void deleteAll(List<Memo> memos);
}
