package com.example.demo.domain.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.user.domain.repository.UniversityRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest(
    properties =
        "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl")
@Import(UniversityPersistenceAdapter.class)
class UniversitySearchPerformanceTest {

  private static final int UNIVERSITY_COUNT = 436;
  private static final int ITERATIONS = 100;

  @Autowired private UniversityRepository universityRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @Test
  void comparesLegacyInMemorySearchWithDatabaseSearch() {
    List<String> universityNames = insertUniversities();
    String keyword = "성능대학교4";

    for (int i = 0; i < 10; i++) {
      searchInMemory(universityNames, keyword);
      universityRepository.searchByName(keyword);
    }

    long memoryStartedAt = System.nanoTime();
    for (int i = 0; i < ITERATIONS; i++) {
      searchInMemory(universityNames, keyword);
    }
    long memoryNanos = System.nanoTime() - memoryStartedAt;

    long databaseStartedAt = System.nanoTime();
    for (int i = 0; i < ITERATIONS; i++) {
      universityRepository.searchByName(keyword);
    }
    long databaseNanos = System.nanoTime() - databaseStartedAt;

    assertThat(universityRepository.searchByName(keyword))
        .hasSameSizeAs(searchInMemory(universityNames, keyword));
    assertThat(memoryNanos).isPositive();
    assertThat(databaseNanos).isPositive();

    System.out.printf(
        Locale.ROOT,
        "university-search benchmark (H2, %,d rows, %,d iterations): memory=%.3fms, db=%.3fms%n",
        UNIVERSITY_COUNT,
        ITERATIONS,
        memoryNanos / 1_000_000.0,
        databaseNanos / 1_000_000.0);
  }

  @Test
  void explainsPartialNameSearchPlan() {
    insertUniversities();

    List<String> plan =
        jdbcTemplate.queryForList(
            """
            EXPLAIN SELECT DISTINCT u.name
            FROM University u
            LEFT JOIN UniversityAlias a ON a.universityId = u.universityId
            WHERE u.active = TRUE
              AND (LOWER(u.name) LIKE LOWER('%성능%')
                OR LOWER(a.aliasName) LIKE LOWER('%성능%'))
            ORDER BY u.name
            """,
            String.class);

    assertThat(plan).isNotEmpty();
    System.out.println("university-search H2 explain plan: " + String.join(" ", plan));
  }

  private List<String> insertUniversities() {
    List<String> names = new ArrayList<>();
    for (int i = 0; i < UNIVERSITY_COUNT; i++) {
      String name = "성능대학교" + i;
      names.add(name);
      jdbcTemplate.update("INSERT INTO University (name, active) VALUES (?, 1)", name);
    }
    return names;
  }

  private List<String> searchInMemory(List<String> names, String keyword) {
    String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
    return names.stream()
        .filter(name -> name.toLowerCase(Locale.ROOT).contains(normalizedKeyword))
        .sorted()
        .toList();
  }
}
