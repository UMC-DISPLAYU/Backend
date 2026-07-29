package com.example.demo.domain.user.domain.repository;

import com.example.demo.domain.user.domain.entity.Agreement;
import java.util.List;

public interface AgreementRepository {
  List<Agreement> findAllSignupAgreements();
}
