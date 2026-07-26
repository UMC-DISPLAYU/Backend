package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.entity.Agreement;
import com.example.demo.domain.user.domain.repository.AgreementRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AgreementPersistenceAdapter implements AgreementRepository {

  private static final List<String> SIGNUP_AGREEMENT_TITLES =
      List.of("위치 기반 서비스 약관", "서비스 이용약관", "개인정보 처리방침", "마케팅 정보 수신 동의");

  private final AgreementJpaRepository agreementJpaRepository;

  @Override
  public Optional<Agreement> findById(Long id) {
    return agreementJpaRepository.findById(id);
  }

  @Override
  public List<Agreement> findAllById(List<Long> ids) { // 추가
    return agreementJpaRepository.findAllById(ids);
  }

  @Override
  public List<Agreement> findAllByOrderByIdAsc() {
    return agreementJpaRepository.findAllByOrderByIdAsc();
  }

  @Override
  public List<Agreement> findAllSignupAgreements() {
    return agreementJpaRepository.findAllByTitleInOrderByIdAsc(SIGNUP_AGREEMENT_TITLES);
  }

  @Override
  public List<Agreement> findAllByIsRequiredTrue() {
    return agreementJpaRepository.findAllByIsRequiredTrue();
  }
}
