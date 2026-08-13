package com.example.demo.domain.archive.infrastructure.persistence.adapter;

import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import com.example.demo.domain.archive.domain.repository.ArchiveArtistRepository;
import com.example.demo.domain.archive.infrastructure.persistence.SpringDataArchiveArtistJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaArchiveArtistRepositoryAdapter implements ArchiveArtistRepository {

  private final SpringDataArchiveArtistJpaRepository jpaRepository;

  public JpaArchiveArtistRepositoryAdapter(SpringDataArchiveArtistJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<ArchiveArtist> findByIdAndUserId(Long archiveArtistId, Long userId) {
    return jpaRepository.findByIdAndUserIdAndDeletedAtIsNull(archiveArtistId, userId);
  }

  @Override
  public Optional<ArchiveArtist> findByUserIdAndArtistProfileId(Long userId, Long artistProfileId) {
    return jpaRepository.findByUserIdAndArtistProfileIdAndDeletedAtIsNull(userId, artistProfileId);
  }

  @Override
  public Optional<ArchiveArtist> findByUserIdAndArtistUserId(Long userId, Long artistUserId) {
    return jpaRepository.findByUserIdAndArtistUserIdAndDeletedAtIsNull(userId, artistUserId);
  }

  @Override
  public List<ArchiveArtist> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
      Long userId, Long cursorId, int limit) {
    return jpaRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
        userId, cursorId, PageRequest.of(0, limit));
  }

  @Override
  public ArchiveArtist save(ArchiveArtist archiveArtist) {
    // 유니크 제약 위반을 save() 호출 시점에 바로 감지하기 위해 flush 시점을 명시적으로 고정한다.
    return jpaRepository.saveAndFlush(archiveArtist);
  }

  @Override
  public void delete(ArchiveArtist archiveArtist) {
    jpaRepository.delete(archiveArtist);
  }
}
