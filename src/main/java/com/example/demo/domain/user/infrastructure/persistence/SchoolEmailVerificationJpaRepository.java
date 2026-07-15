package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolEmailVerificationJpaRepository
    extends JpaRepository<SchoolEmailVerification, Long> {

    Optional<SchoolEmailVerification>
    findTopBySchoolEmailOrderByCreatedAtDesc(
            String schoolEmail
    );

  void deleteBySchoolEmail(String schoolEmail);
}
