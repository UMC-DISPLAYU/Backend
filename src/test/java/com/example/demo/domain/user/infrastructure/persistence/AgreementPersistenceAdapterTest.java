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
  void findsSignupAgreementsByExactCurrentTitles() {
    List<Agreement> agreements =
        List.of(
            agreement(70L, "위치 기반 서비스 약관", true),
            agreement(110L, "서비스 이용약관", true),
            agreement(120L, "개인정보 처리방침", true),
            agreement(130L, "마케팅 정보 수신 동의", false));
    when(agreementJpaRepository.findAllByTitleInOrderByIdAsc(anyCollection()))
        .thenReturn(agreements);

    assertThat(agreementPersistenceAdapter.findAllSignupAgreements()).isEqualTo(agreements);

    ArgumentCaptor<Collection<String>> titlesCaptor = ArgumentCaptor.forClass(Collection.class);
    verify(agreementJpaRepository).findAllByTitleInOrderByIdAsc(titlesCaptor.capture());
    assertThat(titlesCaptor.getValue())
        .containsExactlyInAnyOrder("위치 기반 서비스 약관", "서비스 이용약관", "개인정보 처리방침", "마케팅 정보 수신 동의");
  }

  private static Agreement agreement(Long id, String title, boolean required) {
    return Agreement.builder().id(id).title(title).isRequired(required).build();
  }
}
