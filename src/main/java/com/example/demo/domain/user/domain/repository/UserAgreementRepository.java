package com.example.demo.domain.user.domain.repository;

import com.example.demo.domain.user.domain.entity.UserAgreement;
import java.util.List;

public interface UserAgreementRepository {

  List<UserAgreement> saveAll(List<UserAgreement> userAgreements);
}
