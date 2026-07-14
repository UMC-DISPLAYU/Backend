package com.example.demo.user.infrastructure.persistence;

import com.example.demo.user.domain.entity.UserAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAgreementJpaRepository
        extends JpaRepository<UserAgreement, Long> {

}