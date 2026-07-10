package com.example.demo.user.domain.repository;

import com.example.demo.user.domain.entity.Agreement;

import java.util.List;
import java.util.Optional;

public interface AgreementRepository {

    List<Agreement> findRequiredAgreements();

    Optional<Agreement> findById(Long id);

    List<Agreement> findAllByIsRequiredTrue();

}
