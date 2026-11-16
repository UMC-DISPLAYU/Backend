package com.example.demo.domain.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.domain.entity.Agreement;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class AgreementPersistenceAdapterTest {

  private static final LocalDate AS_OF_DATE = LocalDate.of(2026, 11, 21);
  private final AgreementJpaRepository agreementJpaRepository = mock(AgreementJpaRepository.class);
  private final AgreementPersistenceAdapter agreementPersistenceAdapter =
      new AgreementPersistenceAdapter(
          agreementJpaRepository,
          Clock.fixed(Instant.parse("2026-11-20T15:00:00Z"), ZoneId.of("Asia/Seoul")));

  @Test
  void loadsOnlyTermsOfServiceForSignup() {
    List<Agreement> agreements = List.of(Agreement.builder().id(11L).build());
    when(agreementJpaRepository.findCurrentByCodes(anyCollection(), eq(AS_OF_DATE)))
        .thenReturn(agreements);

    assertThat(agreementPersistenceAdapter.findAllSignupAgreements()).isEqualTo(agreements);

    assertRequestedCodes("TERMS_OF_SERVICE");
  }

  @Test
  void loadsThreePublicDocumentsWithoutLocationBasedService() {
    List<Agreement> agreements = List.of(Agreement.builder().id(11L).build());
    when(agreementJpaRepository.findCurrentByCodes(anyCollection(), eq(AS_OF_DATE)))
        .thenReturn(agreements);

    assertThat(agreementPersistenceAdapter.findAllPublicAgreements()).isEqualTo(agreements);

    assertRequestedCodes("TERMS_OF_SERVICE", "PRIVACY_COLLECTION_USE", "PRIVACY_POLICY");
  }

  private void assertRequestedCodes(String... expectedCodes) {
    ArgumentCaptor<Collection<String>> codesCaptor = ArgumentCaptor.forClass(Collection.class);
    verify(agreementJpaRepository).findCurrentByCodes(codesCaptor.capture(), eq(AS_OF_DATE));
    assertThat(codesCaptor.getValue()).containsExactly(expectedCodes);
  }
}
