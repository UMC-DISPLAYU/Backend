package com.example.demo.domain.archive.infrastructure.persistence.adapter;

import com.example.demo.domain.archive.domain.aggregate.ArchivePersonalWork;
import com.example.demo.domain.archive.domain.repository.ArchivePersonalWorkRepository;
import com.example.demo.domain.archive.infrastructure.persistence.SpringDataArchivePersonalWorkJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaArchivePersonalWorkRepositoryAdapter implements ArchivePersonalWorkRepository {

  private final SpringDataArchivePersonalWorkJpaRepository jpaRepository;

  public JpaArchivePersonalWorkRepositoryAdapter(
      SpringDataArchivePersonalWorkJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<ArchivePersonalWork> findByIdAndUserId(Long archivePersonalWorkId, Long userId) {
    return jpaRepository.findByIdAndUserId(archivePersonalWorkId, userId);
  }

  @Override
  public Optional<ArchivePersonalWork> findByUserIdAndPersonalArtworkId(
      Long userId, Long personalArtworkId) {
    return jpaRepository.findByUserIdAndPersonalArtworkId(userId, personalArtworkId);
  }

  @Override
  public List<ArchivePersonalWork> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      Long userId, LocalDateTime cursorSavedAt, Long cursorSignedId, int limit) {
    return jpaRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
        userId, cursorSavedAt, cursorSignedId, PageRequest.of(0, limit));
  }

  @Override
  public ArchivePersonalWork save(ArchivePersonalWork archivePersonalWork) {
    // 유니크 제약 위반을 save() 호출 시점에 바로 감지하기 위해 flush 시점을 명시적으로 고정한다.
    return jpaRepository.saveAndFlush(archivePersonalWork);
  }

  @Override
  public void delete(ArchivePersonalWork archivePersonalWork) {
    jpaRepository.delete(archivePersonalWork);
  }
}
