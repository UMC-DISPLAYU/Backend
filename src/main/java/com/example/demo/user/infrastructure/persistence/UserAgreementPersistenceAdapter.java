package com.example.demo.user.infrastructure.persistence;

import com.example.demo.user.domain.entity.UserAgreement;
import com.example.demo.user.domain.repository.UserAgreementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserAgreementPersistenceAdapter
        implements UserAgreementRepository {

    private final UserAgreementJpaRepository userAgreementJpaRepository;


    @Override
    public List<UserAgreement> saveAll(
            List<UserAgreement> userAgreements
    ) {
        return userAgreementJpaRepository.saveAll(userAgreements);
    }
}
