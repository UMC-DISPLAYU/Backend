package com.example.demo.domain.user.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.domain.entity.Agreement;
import com.example.demo.domain.user.domain.entity.AgreementPolicy;
import com.example.demo.domain.user.domain.repository.AgreementRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

class AgreementQueryServiceTest {

  private final AgreementRepository agreementRepository = mock(AgreementRepository.class);
  private final AgreementQueryService agreementQueryService =
      new AgreementQueryService(agreementRepository, new AgreementPolicy());

  @Test
  void returnsOnlyFourSignupAgreementsUsingDatabaseIds() {
    when(agreementRepository.findAllSignupAgreements())
        .thenReturn(
            List.of(
                agreement(70L, "위치 기반 서비스 약관", true),
                agreement(110L, "서비스 이용약관", true),
                agreement(120L, "개인정보 처리방침", true),
                agreement(130L, "마케팅 정보 수신 동의", false)));

    var results = agreementQueryService.getAgreements();

    assertThat(results)
        .extracting(result -> result.agreementId())
        .containsExactly(70L, 110L, 120L, 130L);
    assertThat(results)
        .extracting(result -> result.title())
        .containsExactly("위치 기반 서비스 약관", "서비스 이용약관", "개인정보 처리방침", "마케팅 정보 수신 동의");
    assertThat(results)
        .extracting(result -> result.required())
        .containsExactly(true, true, true, false);
    verify(agreementRepository).findAllSignupAgreements();
  }

  private static Agreement agreement(Long id, String title, boolean required) {
    return Agreement.builder().id(id).title(title).isRequired(required).build();
  }
}
