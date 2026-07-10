package com.example.demo.domain.archive.infrastructure.persistence.adapter;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.domain.archive.infrastructure.persistence.SpringDataArchiveDisplayJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaArchiveDisplayRepositoryAdapter implements ArchiveDisplayRepository {

  private final SpringDataArchiveDisplayJpaRepository jpaRepository;

  public JpaArchiveDisplayRepositoryAdapter(SpringDataArchiveDisplayJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<ArchiveDisplay> findById(Long archiveDisplayId) {
    return jpaRepository.findById(archiveDisplayId);
  }

  @Override
  public Optional<ArchiveDisplay> findByUserIdAndDisplayId(Long userId, Long displayId) {
    return jpaRepository.findByUserIdAndDisplayId(userId, displayId);
  }

  @Override
  public List<ArchiveDisplay> findAllByUserIdOrderByIdDesc(Long userId) {
    return jpaRepository.findAllByUserIdOrderByIdDesc(userId);
  }

  @Override
  public ArchiveDisplay save(ArchiveDisplay archiveDisplay) {
    return jpaRepository.save(archiveDisplay);
  }

  @Override
  public void delete(ArchiveDisplay archiveDisplay) {
    jpaRepository.delete(archiveDisplay);
  }
}
