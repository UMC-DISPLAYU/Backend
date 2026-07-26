package com.example.demo.domain.user.domain.repository;

import com.example.demo.domain.user.domain.entity.Agreement;
import java.util.List;
import java.util.Optional;

public interface AgreementRepository {
  Optional<Agreement> findById(Long id);

  List<Agreement> findAllById(List<Long> ids);

  List<Agreement> findAllByOrderByIdAsc();

  List<Agreement> findAllSignupAgreements();

  List<Agreement> findAllByIsRequiredTrue();
}
