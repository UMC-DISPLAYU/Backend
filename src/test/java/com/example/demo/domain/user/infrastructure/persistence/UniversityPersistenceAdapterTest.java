package com.example.demo.domain.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.user.domain.repository.UniversityRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest(
    properties =
        "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl")
@Import(UniversityPersistenceAdapter.class)
class UniversityPersistenceAdapterTest {

  @Autowired private UniversityRepository universityRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUp() {
    insertUniversity("국립경국대학교", true, List.of("안동대학교"));
    insertUniversity("신한대학교", true, List.of());
    insertUniversity("폐교대학교", false, List.of());

    Long universityId = universityId("국립경국대학교");
    jdbcTemplate.update(
        "INSERT INTO UniversityEmailDomain (universityId, domain) VALUES (?, ?)",
        universityId,
        "student.gknu.ac.kr");
    jdbcTemplate.update(
        "INSERT INTO UniversityEmailDomain (universityId, domain) VALUES (?, ?)",
        universityId,
        "student.anu.ac.kr");
  }

  @Test
  void searchesCurrentNameByLegacyAlias() {
    assertThat(universityRepository.searchByName("안동"))
        .extracting(university -> university.getName())
        .containsExactly("국립경국대학교");
  }

  @Test
  void doesNotReturnInactiveUniversity() {
    assertThat(universityRepository.searchByName("폐교")).isEmpty();
  }

  @Test
  void supportsUniversityWithoutEmailDomain() {
    assertThat(universityRepository.searchByName("신한"))
        .extracting(university -> university.getName())
        .containsExactly("신한대학교");
    assertThat(universityRepository.existsEmailDomain(universityId("신한대학교"), "unknown.ac.kr"))
        .isFalse();
  }

  @Test
  void validatesMultipleDomainsWithoutLoadingAllRows() {
    Long universityId = universityId("국립경국대학교");

    assertThat(universityRepository.existsEmailDomain(universityId, "student.gknu.ac.kr")).isTrue();
    assertThat(universityRepository.existsEmailDomain(universityId, "student.anu.ac.kr")).isTrue();
    assertThat(universityRepository.existsEmailDomain(universityId, "snu.ac.kr")).isFalse();
  }

  private void insertUniversity(String name, boolean active, List<String> aliases) {
    jdbcTemplate.update(
        "INSERT INTO University (name, active) VALUES (?, ?)", name, active ? 1 : 0);
    Long universityId = universityId(name);
    aliases.forEach(
        alias ->
            jdbcTemplate.update(
                "INSERT INTO UniversityAlias (universityId, aliasName) VALUES (?, ?)",
                universityId,
                alias));
  }

  private Long universityId(String name) {
    return jdbcTemplate.queryForObject(
        "SELECT universityId FROM University WHERE name = ?", Long.class, name);
  }
}
