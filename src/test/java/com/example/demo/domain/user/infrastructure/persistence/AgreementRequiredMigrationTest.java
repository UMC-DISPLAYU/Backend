package com.example.demo.domain.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.entity.Agreement;
import com.example.demo.domain.user.domain.entity.UserAgreement;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.Type;
import com.example.demo.global.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(
    properties =
        "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl")
class AgreementRequiredMigrationTest {

  private static final String MIGRATION =
      "db/migration/V20261116220000__update_privacy_collection_required.sql";

  @Autowired private EntityManager entityManager;
  @Autowired private DataSource dataSource;
  @Autowired private JdbcTemplate jdbcTemplate;

  @Test
  void updatesOnlyTargetAgreementAndPreservesAgreementHistory() {
    Agreement target =
        agreement(
            "PRIVACY_COLLECTION_USE", "1.0", LocalDate.of(2026, 8, 1), true, "target content");
    Agreement other =
        agreement(
            "PRIVACY_COLLECTION_USE", "2.0", LocalDate.of(2026, 11, 21), true, "other content");
    User user =
        User.builder()
            .provider(Provider.Google)
            .providerId("migration-test-provider-id")
            .name("Migration Test User")
            .nickname("migration-test-user")
            .socialEmail("migration-test@example.com")
            .build();

    entityManager.persist(target);
    entityManager.persist(other);
    entityManager.persist(user);
    UserAgreement history =
        UserAgreement.builder()
            .isAgreed(true)
            .agreedAt(LocalDateTime.of(2026, 8, 1, 12, 0))
            .user(user)
            .agreement(target)
            .build();
    entityManager.persist(history);
    entityManager.flush();

    new ResourceDatabasePopulator(new ClassPathResource(MIGRATION)).execute(dataSource);
    entityManager.clear();

    Map<String, Object> updatedTarget = agreementRow(target.getId());
    assertThat(updatedTarget)
        .containsEntry("isrequired", false)
        .containsEntry("code", "PRIVACY_COLLECTION_USE")
        .containsEntry("version", "1.0")
        .containsEntry("effectivedate", java.sql.Date.valueOf(LocalDate.of(2026, 8, 1)))
        .containsEntry("content", "target content");

    assertThat(agreementRow(other.getId()))
        .containsEntry("isrequired", true)
        .containsEntry("version", "2.0")
        .containsEntry("effectivedate", java.sql.Date.valueOf(LocalDate.of(2026, 11, 21)))
        .containsEntry("content", "other content");

    assertThat(
            jdbcTemplate.queryForMap(
                "SELECT userAgreeId, agreeId, isAgreed, agreedAt FROM UserAgreement WHERE userAgreeId = ?",
                history.getId()))
        .containsEntry("useragreeid", history.getId())
        .containsEntry("agreeid", target.getId())
        .containsEntry("isagreed", true)
        .containsEntry("agreedat", java.sql.Timestamp.valueOf(LocalDateTime.of(2026, 8, 1, 12, 0)));
  }

  private Map<String, Object> agreementRow(Long agreementId) {
    return jdbcTemplate.queryForMap(
        "SELECT code, version, effectiveDate, isRequired, content FROM Agreement WHERE agreeId = ?",
        agreementId);
  }

  private static Agreement agreement(
      String code, String version, LocalDate effectiveDate, boolean required, String content) {
    return Agreement.builder()
        .title(code)
        .type(Type.PRIVACY)
        .content(content)
        .isRequired(required)
        .code(code)
        .version(version)
        .isActive(true)
        .effectiveDate(effectiveDate)
        .displayOrder(2)
        .build();
  }
}
