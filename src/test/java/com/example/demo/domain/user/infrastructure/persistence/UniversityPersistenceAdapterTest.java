package com.example.demo.domain.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.user.application.result.SchoolSearchResult;
import com.example.demo.domain.user.application.service.SearchSchoolService;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UniversityRepository;
import com.example.demo.domain.user.domain.service.SchoolEmailValidator;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@DataJpaTest(
    properties =
        "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl")
@Import({UniversityPersistenceAdapter.class, SearchSchoolService.class, SchoolEmailValidator.class})
class UniversityPersistenceAdapterTest {

  @Autowired private UniversityRepository universityRepository;
  @Autowired private JdbcTemplate jdbcTemplate;
  @Autowired private DataSource dataSource;
  @Autowired private SearchSchoolService searchSchoolService;
  @Autowired private SchoolEmailValidator schoolEmailValidator;

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

  @Test
  void migratedUniversitiesAreSearchableAndAcceptOnlyTheirRegisteredDomains() {
    Long existingUniversityId = universityId("국립경국대학교");
    ResourceDatabasePopulator migration =
        new ResourceDatabasePopulator(
            new ClassPathResource(
                "db/migration/V20261124190000__add_missing_universities_and_email_domains.sql"));

    migration.execute(dataSource);
    migration.execute(dataSource);

    assertThat(searchSchoolService.execute("백석예술"))
        .extracting(SchoolSearchResult::name)
        .containsExactly("백석예술대학교");
    assertThat(searchSchoolService.execute("kdi"))
        .extracting(SchoolSearchResult::name)
        .containsExactly("KDI국제정책대학원대학교");
    assertThat(searchSchoolService.execute("안동"))
        .extracting(SchoolSearchResult::name)
        .containsExactly("국립경국대학교");

    assertThat(schoolEmailValidator.validate("백석예술대학교", "student@bau.ac.kr").getId())
        .isEqualTo(universityId("백석예술대학교"));
    assertThat(schoolEmailValidator.validate("KDI국제정책대학원대학교", "student@kdis.ac.kr").getId())
        .isEqualTo(universityId("KDI국제정책대학원대학교"));
    assertThat(schoolEmailValidator.validate("국립경국대학교", "student@student.gknu.ac.kr").getId())
        .isEqualTo(existingUniversityId);

    assertThatThrownBy(() -> schoolEmailValidator.validate("백석예술대학교", "student@invalid.example"))
        .isInstanceOfSatisfying(
            UserException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(UserErrorCode.SCHOOL_EMAIL_DOMAIN_MISMATCH));
    assertThatThrownBy(() -> schoolEmailValidator.validate("KDI국제정책대학원대학교", "student@bau.ac.kr"))
        .isInstanceOfSatisfying(
            UserException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(UserErrorCode.SCHOOL_EMAIL_DOMAIN_MISMATCH));

    assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM University", Long.class))
        .isEqualTo(5L);
    assertThat(
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM UniversityEmailDomain", Long.class))
        .isEqualTo(4L);
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
