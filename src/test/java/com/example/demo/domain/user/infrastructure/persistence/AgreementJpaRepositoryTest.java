package com.example.demo.domain.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.user.domain.entity.Agreement;
import com.example.demo.domain.user.domain.type.AgreementCode;
import com.example.demo.domain.user.domain.type.Type;
import com.example.demo.global.config.JpaAuditingConfig;
import java.time.LocalDate;
import java.util.Arrays;
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
  void findsOnlyActiveSignupAgreementsOrderedByDisplayOrder() {
    agreementJpaRepository.saveAllAndFlush(
        List.of(
            agreement("활성 서비스 약관", AgreementCode.TERMS_OF_SERVICE, true, 3),
            agreement("활성 개인정보 약관", AgreementCode.PRIVACY_COLLECTION_USE, true, 1),
            agreement("활성 위치 약관", AgreementCode.LOCATION_BASED_SERVICE, true, 2),
            agreement("비활성 서비스 약관", AgreementCode.TERMS_OF_SERVICE, false, 0),
            Agreement.builder()
                .title("활성 마케팅 약관")
                .type(Type.MARKETING)
                .content("마케팅")
                .isRequired(false)
                .code("MARKETING_CONSENT")
                .version("1.0")
                .isActive(true)
                .effectiveDate(LocalDate.of(2026, 8, 1))
                .displayOrder(0)
                .build()));

    List<String> signupCodes = Arrays.stream(AgreementCode.values()).map(Enum::name).toList();

    List<Agreement> results =
        agreementJpaRepository.findAllByCodeInAndIsActiveTrueOrderByDisplayOrderAsc(signupCodes);

    assertThat(results)
        .extracting(Agreement::getTitle)
        .containsExactly("활성 개인정보 약관", "활성 위치 약관", "활성 서비스 약관");
    assertThat(results).allMatch(Agreement::isActive);
    assertThat(results).extracting(Agreement::getDisplayOrder).containsExactly(1, 2, 3);
  }

  private static Agreement agreement(
      String title, AgreementCode code, boolean active, int displayOrder) {
    return Agreement.builder()
        .title(title)
        .type(Type.SERVICE)
        .content(title)
        .isRequired(code != AgreementCode.LOCATION_BASED_SERVICE)
        .code(code.name())
        .version(active ? "1.0" : "0.9")
        .isActive(active)
        .effectiveDate(LocalDate.of(2026, 8, 1))
        .displayOrder(displayOrder)
        .build();
  }
}
