package com.example.demo.domain.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.user.domain.entity.Agreement;
import com.example.demo.domain.user.domain.type.AgreementCode;
import com.example.demo.domain.user.domain.type.Type;
import com.example.demo.global.config.JpaAuditingConfig;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class AgreementJpaRepositoryTest {

  @Autowired private AgreementJpaRepository agreementJpaRepository;

  @Test
  void selectsLatestEffectiveVersionAndExcludesFutureVersion() {
    agreementJpaRepository.saveAllAndFlush(
        List.of(
            agreement("기존 서비스 약관", AgreementCode.TERMS_OF_SERVICE, "1.0", true, 20260801, 1),
            agreement("신규 서비스 약관", AgreementCode.TERMS_OF_SERVICE, "2.0", true, 20261121, 1),
            agreement(
                "개인정보 수집·이용 안내", AgreementCode.PRIVACY_COLLECTION_USE, "2.0", true, 20261121, 2),
            agreement("개인정보 처리방침", AgreementCode.PRIVACY_POLICY, "1.0", true, 20261121, 3),
            agreement(
                "위치기반서비스 이용약관", AgreementCode.LOCATION_BASED_SERVICE, "1.0", true, 20260801, 4),
            agreement("비활성 약관", AgreementCode.TERMS_OF_SERVICE, "3.0", false, 20261101, 0)));

    List<String> publicCodes =
        List.of("TERMS_OF_SERVICE", "PRIVACY_COLLECTION_USE", "PRIVACY_POLICY");

    assertThat(agreementJpaRepository.findCurrentByCodes(publicCodes, LocalDate.of(2026, 11, 20)))
        .extracting(Agreement::getTitle)
        .containsExactly("기존 서비스 약관");

    List<Agreement> effectiveDocuments =
        agreementJpaRepository.findCurrentByCodes(publicCodes, LocalDate.of(2026, 11, 21));

    assertThat(effectiveDocuments)
        .extracting(Agreement::getTitle)
        .containsExactly("신규 서비스 약관", "개인정보 수집·이용 안내", "개인정보 처리방침");
    assertThat(effectiveDocuments)
        .extracting(Agreement::getCode)
        .doesNotContain(AgreementCode.LOCATION_BASED_SERVICE.name());
    assertThat(effectiveDocuments).extracting(Agreement::getDisplayOrder).containsExactly(1, 2, 3);
  }

  private static Agreement agreement(
      String title,
      AgreementCode code,
      String version,
      boolean active,
      int effectiveDate,
      int displayOrder) {
    return Agreement.builder()
        .title(title)
        .type(code == AgreementCode.TERMS_OF_SERVICE ? Type.SERVICE : Type.PRIVACY)
        .content(title)
        .isRequired(code == AgreementCode.TERMS_OF_SERVICE)
        .code(code.name())
        .version(version)
        .isActive(active)
        .effectiveDate(
            LocalDate.of(effectiveDate / 10000, effectiveDate / 100 % 100, effectiveDate % 100))
        .displayOrder(displayOrder)
        .build();
  }
}
