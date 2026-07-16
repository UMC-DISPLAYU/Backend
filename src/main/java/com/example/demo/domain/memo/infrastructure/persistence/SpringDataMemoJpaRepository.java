package com.example.demo.domain.memo.infrastructure.persistence;

import com.example.demo.domain.memo.domain.aggregate.Memo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataMemoJpaRepository extends JpaRepository<Memo, Long> {

  Optional<Memo> findByArchiveDisplayIdAndDeletedAtIsNull(Long archiveDisplayId);

  Optional<Memo> findByArchiveWorkIdAndDeletedAtIsNull(Long archiveWorkId);
}
