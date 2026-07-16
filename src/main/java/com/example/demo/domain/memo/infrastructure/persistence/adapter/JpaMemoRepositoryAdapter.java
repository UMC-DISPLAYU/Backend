package com.example.demo.domain.memo.infrastructure.persistence.adapter;

import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.domain.memo.infrastructure.persistence.SpringDataMemoJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaMemoRepositoryAdapter implements MemoRepository {

  private final SpringDataMemoJpaRepository jpaRepository;

  public JpaMemoRepositoryAdapter(SpringDataMemoJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<Memo> findByArchiveDisplayIdAndDeletedAtIsNull(Long archiveDisplayId) {
    return jpaRepository.findByArchiveDisplayIdAndDeletedAtIsNull(archiveDisplayId);
  }

  @Override
  public Optional<Memo> findByArchiveWorkIdAndDeletedAtIsNull(Long archiveWorkId) {
    return jpaRepository.findByArchiveWorkIdAndDeletedAtIsNull(archiveWorkId);
  }

  @Override
  public Memo save(Memo memo) {
    return jpaRepository.save(memo);
  }
}
