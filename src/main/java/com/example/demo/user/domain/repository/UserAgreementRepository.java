package com.example.demo.user.domain.repository;

import com.example.demo.user.domain.entity.UserAgreement;

import java.util.List;

public interface UserAgreementRepository {

    List<UserAgreement> saveAll(
            List<UserAgreement> userAgreements
    );
}