package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.entity.Agreement;
import com.example.demo.domain.user.domain.enums.AgreementCode;
import com.example.demo.domain.user.domain.repository.AgreementRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AgreementPersistenceAdapter implements AgreementRepository {

  private static final List<String> SIGNUP_AGREEMENT_CODES =
      Arrays.stream(AgreementCode.values()).map(Enum::name).toList();

  private final AgreementJpaRepository agreementJpaRepository;

  @Override
  public List<Agreement> findAllSignupAgreements() {
    return agreementJpaRepository.findAllByCodeInAndIsActiveTrueOrderByDisplayOrderAsc(
        SIGNUP_AGREEMENT_CODES);
  }
}
