package com.example.demo.domain.memo.infrastructure.persistence.adapter;

import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.domain.memo.infrastructure.persistence.SpringDataMemoJpaRepository;
import java.util.List;
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
  public List<Memo> findByArchiveDisplayIdInAndDeletedAtIsNull(List<Long> archiveDisplayIds) {
    return jpaRepository.findByArchiveDisplayIdInAndDeletedAtIsNull(archiveDisplayIds);
  }

  @Override
  public List<Memo> findByArchiveWorkIdInAndDeletedAtIsNull(List<Long> archiveWorkIds) {
    return jpaRepository.findByArchiveWorkIdInAndDeletedAtIsNull(archiveWorkIds);
  }

  @Override
  public List<Memo> findAllByArchiveDisplayId(Long archiveDisplayId) {
    return jpaRepository.findAllByArchiveDisplayId(archiveDisplayId);
  }

  @Override
  public List<Memo> findAllByArchiveWorkId(Long archiveWorkId) {
    return jpaRepository.findAllByArchiveWorkId(archiveWorkId);
  }

  @Override
  public Memo save(Memo memo) {
    // 유니크 제약 위반을 save() 호출 시점에 바로 감지하기 위해 flush 시점을 명시적으로 고정한다.
    return jpaRepository.saveAndFlush(memo);
  }

  @Override
  public void deleteAll(List<Memo> memos) {
    jpaRepository.deleteAll(memos);
  }
}
