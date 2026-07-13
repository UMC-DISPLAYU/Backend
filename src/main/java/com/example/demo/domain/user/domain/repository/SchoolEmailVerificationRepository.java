package com.example.demo.domain.user.domain.repository;

import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import java.util.Optional;

public interface SchoolEmailVerificationRepository {

    SchoolEmailVerification save(SchoolEmailVerification verification);

    Optional<SchoolEmailVerification> findBySchoolEmail(String schoolEmail);

    void deleteBySchoolEmail(String schoolEmail);
}