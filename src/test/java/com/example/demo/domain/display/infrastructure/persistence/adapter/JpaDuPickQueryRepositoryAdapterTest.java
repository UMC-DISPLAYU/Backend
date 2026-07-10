package com.example.demo.domain.display.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.DuPickQuery;
import com.example.demo.domain.display.application.query.DuPickQueryRepository;
import com.example.demo.domain.display.application.query.DuPickQueryResult;
import com.example.demo.domain.display.infrastructure.persistence.DuPickColumnJpaEntity;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDuPickQueryJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaDuPickQueryRepositoryAdapter.class)
class JpaDuPickQueryRepositoryAdapterTest {

  @Autowired private DuPickQueryRepository queryRepository;

  @Autowired private SpringDataDuPickQueryJpaRepository jpaRepository;

  @Test
  void findDuPicksReturnsColumnsOrderedByColumnIdAsc() {
    LocalDateTime createdAt = LocalDateTime.of(2026, 7, 1, 11, 0);
    jpaRepository.saveAllAndFlush(
        List.of(
            duPick(3L, "세 번째", createdAt.plusDays(2)),
            duPick(1L, "첫 번째", createdAt),
            duPick(2L, "두 번째", createdAt.plusDays(1))));

    List<DuPickQueryResult> results = queryRepository.findDuPicks(new DuPickQuery(null, 2), 2);

    assertThat(results).extracting(DuPickQueryResult::duPickId).containsExactly(1L, 2L);
    assertThat(results.getFirst().subtitle()).isEqualTo("첫 번째 내용");
    assertThat(results.getFirst().authorName()).isEqualTo("에디터 디유");
  }

  @Test
  void findDuPicksAppliesCursor() {
    LocalDateTime createdAt = LocalDateTime.of(2026, 7, 1, 11, 0);
    jpaRepository.saveAllAndFlush(
        List.of(
            duPick(1L, "첫 번째", createdAt),
            duPick(2L, "두 번째", createdAt.plusDays(1)),
            duPick(3L, "세 번째", createdAt.plusDays(2))));

    List<DuPickQueryResult> results = queryRepository.findDuPicks(new DuPickQuery(1L, 10), 10);

    assertThat(results).extracting(DuPickQueryResult::duPickId).containsExactly(2L, 3L);
  }

  private static DuPickColumnJpaEntity duPick(Long id, String name, LocalDateTime createdAt) {
    return new DuPickColumnJpaEntity(
        id,
        name,
        name + " 내용",
        "https://cdn.displayu.com/home/du_pick_" + id + ".png",
        createdAt,
        createdAt);
  }
}
