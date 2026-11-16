package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.entity.Agreement;
import com.example.demo.domain.user.domain.repository.AgreementRepository;
import com.example.demo.domain.user.domain.type.AgreementCode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AgreementPersistenceAdapter implements AgreementRepository {

  private static final List<String> SIGNUP_AGREEMENT_CODES =
      List.of(AgreementCode.TERMS_OF_SERVICE.name());
  private static final List<String> PUBLIC_AGREEMENT_CODES =
      List.of(
          AgreementCode.TERMS_OF_SERVICE.name(),
          AgreementCode.PRIVACY_COLLECTION_USE.name(),
          AgreementCode.PRIVACY_POLICY.name());

  private final AgreementJpaRepository agreementJpaRepository;
  private final Clock clock;

  @Override
  public List<Agreement> findAllSignupAgreements() {
    return findCurrentAgreements(SIGNUP_AGREEMENT_CODES);
  }

  @Override
  public List<Agreement> findAllPublicAgreements() {
    return findCurrentAgreements(PUBLIC_AGREEMENT_CODES);
  }

  private List<Agreement> findCurrentAgreements(List<String> codes) {
    return agreementJpaRepository.findCurrentByCodes(codes, LocalDate.now(clock));
  }
}
