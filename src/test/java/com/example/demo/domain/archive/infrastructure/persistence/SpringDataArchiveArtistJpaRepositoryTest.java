package com.example.demo.domain.archive.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import com.example.demo.global.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class SpringDataArchiveArtistJpaRepositoryTest {

  @Autowired private SpringDataArchiveArtistJpaRepository repository;

  @Autowired private EntityManager entityManager;

  @Test
  void findByUserIdBeforeCursorContinuesWhenCursorRecordIsDeleted() {
    ArchiveArtist cursor = repository.saveAndFlush(ArchiveArtist.create(100L, 200L, 7L));
    ArchiveArtist next = repository.saveAndFlush(ArchiveArtist.create(101L, 201L, 7L));

    updateSavedAt(cursor.getId(), LocalDateTime.of(2026, 8, 13, 12, 0));
    updateSavedAt(next.getId(), LocalDateTime.of(2026, 8, 13, 11, 0));
    cursor.delete();
    repository.saveAndFlush(cursor);
    entityManager.clear();

    List<ArchiveArtist> results =
        repository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            7L, cursor.getId(), PageRequest.of(0, 10));

    assertThat(results).extracting(ArchiveArtist::getId).containsExactly(next.getId());
  }

  private void updateSavedAt(Long id, LocalDateTime savedAt) {
    entityManager
        .createQuery("UPDATE ArchiveArtist a SET a.savedAt = :savedAt WHERE a.id = :id")
        .setParameter("savedAt", savedAt)
        .setParameter("id", id)
        .executeUpdate();
  }
}
