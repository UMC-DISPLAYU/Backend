package com.example.demo.domain.archive.infrastructure.persistence.adapter;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.domain.archive.infrastructure.persistence.SpringDataArchiveDisplayJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaArchiveDisplayRepositoryAdapter implements ArchiveDisplayRepository {

  private final SpringDataArchiveDisplayJpaRepository jpaRepository;

  public JpaArchiveDisplayRepositoryAdapter(SpringDataArchiveDisplayJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<ArchiveDisplay> findById(Long archiveDisplayId) {
    return jpaRepository.findById(archiveDisplayId).filter(archive -> !archive.isDeleted());
  }

  @Override
  public Optional<ArchiveDisplay> findByIdAndUserId(Long archiveDisplayId, Long userId) {
    return jpaRepository.findByIdAndUserIdAndDeletedAtIsNull(archiveDisplayId, userId);
  }

  @Override
  public Optional<ArchiveDisplay> findByUserIdAndDisplayId(Long userId, Long displayId) {
    return jpaRepository.findByUserIdAndDisplayIdAndDeletedAtIsNull(userId, displayId);
  }

  @Override
  public List<Long> findDisplayIdsByUserIdAndDisplayIdIn(Long userId, List<Long> displayIds) {
    if (displayIds.isEmpty()) {
      return List.of();
    }
    return jpaRepository.findDisplayIdsByUserIdAndDisplayIdIn(userId, displayIds);
  }

  @Override
  public List<ArchiveDisplay> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      Long userId, Long cursorId, int limit) {
    return jpaRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
        userId, cursorId, PageRequest.of(0, limit));
  }

  @Override
  public ArchiveDisplay save(ArchiveDisplay archiveDisplay) {
    // 유니크 제약 위반을 save() 호출 시점에 바로 감지하기 위해 flush 시점을 명시적으로 고정한다.
    return jpaRepository.saveAndFlush(archiveDisplay);
  }

  @Override
  public void delete(ArchiveDisplay archiveDisplay) {
    jpaRepository.delete(archiveDisplay);
  }
}
