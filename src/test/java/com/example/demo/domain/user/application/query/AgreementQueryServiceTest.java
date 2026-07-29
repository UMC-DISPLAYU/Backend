package com.example.demo.domain.user.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.domain.entity.Agreement;
import com.example.demo.domain.user.domain.entity.AgreementPolicy;
import com.example.demo.domain.user.domain.enums.AgreementCode;
import com.example.demo.domain.user.domain.enums.Type;
import com.example.demo.domain.user.domain.repository.AgreementRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class AgreementQueryServiceTest {

  private final AgreementRepository agreementRepository = mock(AgreementRepository.class);
  private final AgreementQueryService agreementQueryService =
      new AgreementQueryService(agreementRepository, new AgreementPolicy());

  @Test
  void returnsThreeActiveSignupAgreementsInDisplayOrder() {
    when(agreementRepository.findAllSignupAgreements())
        .thenReturn(
            List.of(
                agreement(11L, AgreementCode.TERMS_OF_SERVICE, Type.SERVICE, true, 1),
                agreement(12L, AgreementCode.PRIVACY_COLLECTION_USE, Type.PRIVACY, true, 2),
                agreement(7L, AgreementCode.LOCATION_BASED_SERVICE, Type.SERVICE, false, 3)));

    var results = agreementQueryService.getAgreements();

    assertThat(results).extracting(result -> result.agreementId()).containsExactly(11L, 12L, 7L);
    assertThat(results)
        .extracting(result -> result.code())
        .containsExactly("TERMS_OF_SERVICE", "PRIVACY_COLLECTION_USE", "LOCATION_BASED_SERVICE");
    assertThat(results).extracting(result -> result.required()).containsExactly(true, true, false);
    assertThat(results).extracting(result -> result.displayOrder()).containsExactly(1, 2, 3);
    verify(agreementRepository).findAllSignupAgreements();
  }

  private static Agreement agreement(
      Long id, AgreementCode code, Type type, boolean required, int displayOrder) {
    return Agreement.builder()
        .id(id)
        .code(code.name())
        .title(code.name())
        .type(type)
        .content("content")
        .isRequired(required)
        .version("1.0")
        .isActive(true)
        .effectiveDate(LocalDate.of(2026, 8, 1))
        .displayOrder(displayOrder)
        .build();
  }
}
