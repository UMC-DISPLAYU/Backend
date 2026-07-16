package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.entity.UserAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAgreementJpaRepository extends JpaRepository<UserAgreement, Long> {}
