package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface SchoolEmailVerificationJpaRepository
        extends JpaRepository<SchoolEmailVerification, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SchoolEmailVerification> findTopBySchoolEmailOrderByCreatedAtDesc(
            String schoolEmail
    );

    void deleteBySchoolEmail(String schoolEmail);
}
