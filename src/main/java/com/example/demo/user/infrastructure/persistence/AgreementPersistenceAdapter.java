package com.example.demo.user.infrastructure.persistence;

import com.example.demo.user.domain.entity.Agreement;
import com.example.demo.user.domain.repository.AgreementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
