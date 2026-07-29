package com.example.demo.domain.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.domain.entity.Agreement;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class AgreementPersistenceAdapterTest {

  private final AgreementJpaRepository agreementJpaRepository = mock(AgreementJpaRepository.class);
  private final AgreementPersistenceAdapter agreementPersistenceAdapter =
      new AgreementPersistenceAdapter(agreementJpaRepository);

  @Test
  void findsOnlyActiveSignupAgreementCodesInDisplayOrder() {
    List<Agreement> agreements = List.of(Agreement.builder().id(11L).build());
    when(agreementJpaRepository.findAllByCodeInAndIsActiveTrueOrderByDisplayOrderAsc(
            anyCollection()))
        .thenReturn(agreements);

    assertThat(agreementPersistenceAdapter.findAllSignupAgreements()).isEqualTo(agreements);

    ArgumentCaptor<Collection<String>> codesCaptor = ArgumentCaptor.forClass(Collection.class);
    verify(agreementJpaRepository)
        .findAllByCodeInAndIsActiveTrueOrderByDisplayOrderAsc(codesCaptor.capture());
    assertThat(codesCaptor.getValue())
        .containsExactlyInAnyOrder(
            "TERMS_OF_SERVICE", "PRIVACY_COLLECTION_USE", "LOCATION_BASED_SERVICE");
  }
}
