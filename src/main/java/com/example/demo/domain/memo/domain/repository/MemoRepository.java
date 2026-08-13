package com.example.demo.domain.memo.domain.repository;

import com.example.demo.domain.memo.domain.aggregate.Memo;
import java.util.List;
import java.util.Optional;

public interface MemoRepository {

  Optional<Memo> findByArchiveDisplayIdAndDeletedAtIsNull(Long archiveDisplayId);

  Optional<Memo> findByArchiveWorkIdAndDeletedAtIsNull(Long archiveWorkId);

  Optional<Memo> findByArchivePersonalWorkIdAndDeletedAtIsNull(Long archivePersonalWorkId);

  List<Memo> findByArchiveDisplayIdInAndDeletedAtIsNull(List<Long> archiveDisplayIds);

  List<Memo> findByArchiveWorkIdInAndDeletedAtIsNull(List<Long> archiveWorkIds);

  List<Memo> findByArchivePersonalWorkIdInAndDeletedAtIsNull(List<Long> archivePersonalWorkIds);

  Memo save(Memo memo);
}
