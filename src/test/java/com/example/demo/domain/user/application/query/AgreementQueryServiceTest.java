package com.example.demo.domain.user.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.domain.entity.Agreement;
import com.example.demo.domain.user.domain.repository.AgreementRepository;
import com.example.demo.domain.user.domain.type.AgreementCode;
import com.example.demo.domain.user.domain.type.Type;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class AgreementQueryServiceTest {

  private final AgreementRepository agreementRepository = mock(AgreementRepository.class);
  private final AgreementQueryService agreementQueryService =
      new AgreementQueryService(agreementRepository);

  @Test
  void returnsThreePublicDocumentsInDisplayOrder() {
    when(agreementRepository.findAllPublicAgreements())
        .thenReturn(
            List.of(
                agreement(11L, AgreementCode.TERMS_OF_SERVICE, Type.SERVICE, true, 1),
                agreement(12L, AgreementCode.PRIVACY_COLLECTION_USE, Type.PRIVACY, false, 2),
                agreement(13L, AgreementCode.PRIVACY_POLICY, Type.PRIVACY, false, 3)));

    var results = agreementQueryService.getAgreements();

    assertThat(results).extracting(result -> result.agreementId()).containsExactly(11L, 12L, 13L);
    assertThat(results)
        .extracting(result -> result.code())
        .containsExactly("TERMS_OF_SERVICE", "PRIVACY_COLLECTION_USE", "PRIVACY_POLICY");
    assertThat(results).extracting(result -> result.required()).containsExactly(true, false, false);
    assertThat(results).extracting(result -> result.displayOrder()).containsExactly(1, 2, 3);
    verify(agreementRepository).findAllPublicAgreements();
  }

  private static Agreement agreement(
      Long id, AgreementCode code, Type type, boolean required, int displayOrder) {
    return Agreement.builder()
        .id(id)
        .code(code.name())
        .title(code.name())
        .type(type)
        .content(
            code == AgreementCode.PRIVACY_COLLECTION_USE
                ? "보유 기간: 탈퇴할 때까지 (후기·방명록·댓글은 아래 4번처럼 남음)"
                : "content")
        .isRequired(required)
        .version("1.0")
        .isActive(true)
        .effectiveDate(LocalDate.of(2026, 11, 21))
        .displayOrder(displayOrder)
        .build();
  }
}
