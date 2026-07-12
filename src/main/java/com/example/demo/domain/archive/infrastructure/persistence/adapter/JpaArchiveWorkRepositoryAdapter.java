package com.example.demo.domain.archive.infrastructure.persistence.adapter;

import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.domain.archive.infrastructure.persistence.SpringDataArchiveWorkJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaArchiveWorkRepositoryAdapter implements ArchiveWorkRepository {

  private final SpringDataArchiveWorkJpaRepository jpaRepository;

  public JpaArchiveWorkRepositoryAdapter(SpringDataArchiveWorkJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<ArchiveWork> findById(Long archiveWorkId) {
    return jpaRepository.findById(archiveWorkId);
  }

  @Override
  public Optional<ArchiveWork> findByUserIdAndDisplayArtworkId(Long userId, Long displayArtworkId) {
    return jpaRepository.findByUserIdAndDisplayArtworkId(userId, displayArtworkId);
  }

  @Override
  public List<ArchiveWork> findAllByUserIdOrderBySavedAtDescIdDesc(Long userId) {
    return jpaRepository.findAllByUserIdOrderBySavedAtDescIdDesc(userId);
  }

  @Override
  public ArchiveWork save(ArchiveWork archiveWork) {
    // 유니크 제약 위반을 save() 호출 시점에 바로 감지하기 위해 flush 시점을 명시적으로 고정한다.
    return jpaRepository.saveAndFlush(archiveWork);
  }

  @Override
  public void delete(ArchiveWork archiveWork) {
    jpaRepository.delete(archiveWork);
  }
}
