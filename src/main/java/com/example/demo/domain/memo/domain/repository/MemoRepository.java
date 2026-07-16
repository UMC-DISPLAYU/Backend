package com.example.demo.domain.memo.domain.repository;

import com.example.demo.domain.memo.domain.aggregate.Memo;
import java.util.Optional;

public interface MemoRepository {

  Optional<Memo> findByArchiveDisplayIdAndDeletedAtIsNull(Long archiveDisplayId);

  Optional<Memo> findByArchiveWorkIdAndDeletedAtIsNull(Long archiveWorkId);

  Memo save(Memo memo);
}
