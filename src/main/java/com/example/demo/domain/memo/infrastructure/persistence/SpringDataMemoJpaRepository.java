package com.example.demo.domain.memo.infrastructure.persistence;

import com.example.demo.domain.memo.domain.aggregate.Memo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataMemoJpaRepository extends JpaRepository<Memo, Long> {

  Optional<Memo> findByArchiveDisplayIdAndDeletedAtIsNull(Long archiveDisplayId);

  Optional<Memo> findByArchiveWorkIdAndDeletedAtIsNull(Long archiveWorkId);

  Optional<Memo> findByArchivePersonalWorkIdAndDeletedAtIsNull(Long archivePersonalWorkId);

  List<Memo> findByArchiveDisplayIdInAndDeletedAtIsNull(List<Long> archiveDisplayIds);

  List<Memo> findByArchiveWorkIdInAndDeletedAtIsNull(List<Long> archiveWorkIds);

  List<Memo> findByArchivePersonalWorkIdInAndDeletedAtIsNull(List<Long> archivePersonalWorkIds);
}
