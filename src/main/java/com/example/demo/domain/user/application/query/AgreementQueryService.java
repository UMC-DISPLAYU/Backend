package com.example.demo.domain.user.application.query;

import com.example.demo.domain.user.application.result.AgreementResult;
import com.example.demo.domain.user.domain.entity.Agreement;
import com.example.demo.domain.user.domain.entity.AgreementPolicy;
import com.example.demo.domain.user.domain.repository.AgreementRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgreementQueryService {

  private final AgreementRepository agreementRepository;
  private final AgreementPolicy agreementPolicy;

  @Transactional(readOnly = true)
  public List<AgreementResult> getAgreements() {
    List<Agreement> signupAgreements = agreementRepository.findAllSignupAgreements();
    agreementPolicy.validateSignupConfiguration(signupAgreements);

    return signupAgreements.stream()
        .map(
            agreement ->
                new AgreementResult(
                    agreement.getId(), agreement.getTitle(), agreement.isRequired()))
        .toList();
  }
}
