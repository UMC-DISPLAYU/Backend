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

  private final AgreementJpaRepository agreementJpaRepository;

  @Override
  public List<Agreement> findRequiredAgreements() {
    return agreementJpaRepository.findByIsRequiredTrue();
  }

  @Override
  public Optional<Agreement> findById(Long id) {
    return agreementJpaRepository.findById(id);
  }

  @Override
  public List<Agreement> findAllByIsRequiredTrue() {
    return agreementJpaRepository.findAllByIsRequiredTrue();
  }
}
